package org.usfirst.frc.team2073.robot.subsystems.shooter;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.team2073.common.motionprofiling.MotionProfileHelper;
import com.team2073.common.motionprofiling.TrajectoryPointPusher;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.speedcontrollers.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.shooter.StopShooterPivotCommand;
import org.usfirst.frc.team2073.robot.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Defaults;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Elevator;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Shooter;
import org.usfirst.frc.team2073.robot.domain.MotionProfileConfiguration;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

import java.util.List;

@Singleton
public class ShooterPivotSubsystem extends Subsystem implements SmartDashboardAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EagleSRX pivotMotor;
    private final MotionProfileHelper pivotMotorHelper;

    private TrajectoryPointPusher trajPointPusher;
    private boolean reachedMinPoints;
    private DigitalInput shooterPivotLimit;

    @Inject
    ElevatorSubsystem elevatorSys;

    @Inject
    private Provider<StopShooterPivotCommand> stopShooterPivot;

    private double pgain = Shooter.PIVOT_P_GAIN;
    private double igain = Shooter.PIVOT_I_GAIN;
    private double dgain = Shooter.PIVOT_D_GAIN;

    private State state = State.INITIALIZING;
    private SetPoint positionState = SetPoint.StartingConfiguration;

    private boolean hasZeroed = false;

    enum State {
        INITIALIZING,
        STARTING_PUSH,
        HOLDING,
        MOVING,
    }

    enum SetPoint {
        StartingConfiguration,
        AtCorrectPosition,
        MovingToFront,
        MovingToFrontAngle,
        MovingToBackAngle,
        MovingToBack,
        ErrorIncorrect,
    }


    @Inject
    public ShooterPivotSubsystem(@Named("shooterPivotMotor") EagleSRX pivotMotor, @Named("shooterPivotLimit") DigitalInput shooterPivotLimit) {
        this.pivotMotor = pivotMotor;
        this.shooterPivotLimit = shooterPivotLimit;
        pivotMotorHelper = new MotionProfileHelper(pivotMotor, !Shooter.PIVOT_DEFAULT_DIRECTION, "Shooter F Gain", Shooter.PIVOT_F_GAIN, "Shooter Pivot");
        configEncoder();
        state = State.INITIALIZING;
        setPIDGains();
        pivotMotor.configPeakOutputForward(.6, 10);
        pivotMotor.configPeakOutputReverse(-.6, 10);
        pivotMotor.configNominalOutputForward(0, 10);
        pivotMotor.configNominalOutputReverse(0, 10);
        pivotMotor.setInverted(Shooter.PIVOT_DEFAULT_DIRECTION);
    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(stopShooterPivot.get());
    }

    public void setBrakeMode() {
        pivotMotor.setNeutralMode(NeutralMode.Brake);
    }

    public void setCoastMode() {
        pivotMotor.setNeutralMode(NeutralMode.Coast);
    }

    public void zeroEncoder() {
        if (!hasZeroed) {
            pivotMotor.setSelectedSensorPosition(0, 0, 10);
            hasZeroed = true;
        }
    }

    private int convertAngleToEncoderTics(double angle) {
        return (int) Math.round((angle / 360) * Shooter.ENCODER_EDGES_PER_REVOLUTION * Shooter.PIVOT_TO_ENCODER_RATIO);
    }

    public void stopMotor() {
        pivotMotor.set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_P_GAIN, Defaults.INTAKE_P_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_I_GAIN, Defaults.INTAKE_I_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_D_GAIN, Defaults.INTAKE_D_GAIN);
        SmartDashboard.putString(Shooter.NAME + " Pivot State", state.toString());
        SmartDashboard.putString(Shooter.NAME + " Pivot CurrentPosition", positionState.toString());
    }

    @Override
    public void readSmartDashboard() {
        pgain = SmartDashboard.getNumber(DashboardKeys.INTAKE_P_GAIN, Defaults.DRIVETRAIN_P_GAIN);
        igain = SmartDashboard.getNumber(DashboardKeys.INTAKE_I_GAIN, Defaults.DRIVETRAIN_I_GAIN);
        dgain = SmartDashboard.getNumber(DashboardKeys.INTAKE_D_GAIN, Defaults.DRIVETRAIN_D_GAIN);
    }

    public void setPIDGains() {
        pivotMotor.config_kF(0, Shooter.PIVOT_F_GAIN, 10);
        pivotMotor.config_kP(0, pgain, 0);
        pivotMotor.config_kI(0, igain, 0);
        pivotMotor.config_kD(0, dgain, 0);
    }

    public void configEncoder() {
        pivotMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        pivotMotor.configAllowableClosedloopError(0, 5, 10);
    }

    public MotionProfileConfiguration rotationConfiguration(double revolutions) {
        MotionProfileConfiguration configuration = new MotionProfileConfiguration();
        double encoderTics = revolutions * Shooter.ENCODER_EDGES_PER_REVOLUTION;
        configuration.setEndDistance(encoderTics);
        configuration.setIntervalVal(5);
        configuration.setInterval(TrajectoryDuration.Trajectory_Duration_5ms);
        configuration.setMaxVel(Shooter.PIVOT_MAX_VELOCITY);
        configuration.setMaxAcc(Shooter.PIVOT_MAX_ACCELERATION);
        configuration.setVelocityOnly(false);
        return configuration;
    }

    public void resetMotionProfiling(List<TrajectoryPoint> trajPointList, boolean forwards) {
        pivotMotorHelper.reset();
        trajPointPusher = new TrajectoryPointPusher(pivotMotorHelper,
                trajPointList);
        state = State.STARTING_PUSH;
        new Thread(trajPointPusher).start();
        pivotMotorHelper.checkDirection(forwards);
    }

    public void processMotionProfiling() {
        if (pivotMotorHelper.isBufferSufficentlyFull(Defaults.MINIMUM_POINTS_TO_RUN * 5))
            reachedMinPoints = true;
        if (reachedMinPoints) {
            pivotMotorHelper.processPoints();
            state = State.MOVING;
            System.out.println(pivotMotor.getMotorOutputVoltage());
        }
    }

    public void stopMotionProfiling() {
        logger.debug("Finished");
        trajPointPusher.interrupt();
        pivotMotorHelper.stopTalon();
        reachedMinPoints = false;
        state = State.HOLDING;
    }

    public boolean isMotionProfilingFinished() {
        return reachedMinPoints && pivotMotorHelper.isFinished();
    }

    public double getAngularPosition() {
        return (pivotMotor.getSelectedSensorPosition(0)
                / (Shooter.PIVOT_TO_ENCODER_RATIO * Shooter.ENCODER_EDGES_PER_REVOLUTION)) * 360;
    }

    public boolean aboutEquals(double val1, double val2) {
        return val1 >= val2 - 1 && val1 <= val2 + 1;
    }

    public void spinForward() {
        pivotMotor.set(ControlMode.PercentOutput, .50);
    }

    public void spinBackward() {
        pivotMotor.set(ControlMode.PercentOutput, -.15);
    }

    private double setPoint;

    public void pidAngularSetPoint(double angle) {
        double position = (angle / 360) * Shooter.PIVOT_TO_ENCODER_RATIO * Shooter.ENCODER_EDGES_PER_REVOLUTION;
        pivotMotor.selectProfileSlot(1, 0);
        configPIDProfileGains();
        this.setPoint = position;
    }

    public void periodicPID() {
        if (!(((elevatorSys.getCurrentHeight() <= Elevator.BOTTOM_HEIGHT + 1) && setPoint < convertAngleToEncoderTics(100))
                || ((elevatorSys.getCurrentHeight() >= Elevator.MAX_TRAVEL - 2)))
                && (setPoint > convertAngleToEncoderTics(60))) {

            pivotMotor.set(ControlMode.PercentOutput, 0);
        } else if (!(elevatorSys.getCurrentHeight() >= Elevator.MAX_TRAVEL - 2) && setPoint > convertAngleToEncoderTics(70)) {
            pivotMotor.set(ControlMode.Position, convertAngleToEncoderTics(60));
        } else
            pivotMotor.set(ControlMode.Position, setPoint);
    }


    public void pidEncoderSetPoint(double position) {
        pivotMotor.selectProfileSlot(1, 0);
        configPIDProfileGains();
        pivotMotor.set(ControlMode.Position, position);
    }


    private void configPIDProfileGains() {
        pivotMotor.config_kP(1, Shooter.PIVOT_HOLD_P, 10);
        pivotMotor.config_kI(1, Shooter.PIVOT_HOLD_I, 10);
        pivotMotor.config_kD(1, Shooter.PIVOT_HOLD_D, 10);
    }

    public boolean isAtZeroSensor() {
        return !shooterPivotLimit.get();
    }

    public boolean isPivotBack() {
        return getAngularPosition() > 60;
    }

    public boolean hasReachedSetpoint(double setPoint) {
        return aboutEquals(getAngularPosition(), setPoint);
    }

}
