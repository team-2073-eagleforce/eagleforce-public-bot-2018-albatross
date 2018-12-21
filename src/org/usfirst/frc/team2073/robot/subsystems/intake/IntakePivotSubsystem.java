package org.usfirst.frc.team2073.robot.subsystems.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.speedcontrollers.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.intake.FollowerIntakeModeCommand;
import org.usfirst.frc.team2073.robot.conf.AppConstants;
import org.usfirst.frc.team2073.robot.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Defaults;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Intake;

@Singleton
public class IntakePivotSubsystem extends Subsystem implements SmartDashboardAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EagleSRX pivotRightMotor;
    private final EagleSRX pivotLeftMotor;
    private final Joystick controller;
    private final DigitalInput leftLimit;
    private final DigitalInput rightLimit;

    private double pgain = Defaults.INTAKE_P_GAIN;
    private double igain = Defaults.INTAKE_I_GAIN;
    private double dgain = Defaults.INTAKE_D_GAIN;

    private State state = State.Initializing;

    enum State {
        Initializing,
        Holding,
        Moving,
        Zero
    }

    @Inject
    public IntakePivotSubsystem(Provider<FollowerIntakeModeCommand> defaultCommandProvider,
                                @Named("rightPivotMotor") EagleSRX pivotRightMotor, @Named("leftPivotMotor") EagleSRX pivotLeftMotor,
                                @Named("leftPivotLimit") DigitalInput leftPivotLimit,
                                @Named("rightPivotLimit") DigitalInput rightPivotLimit, @Named("controller") Joystick controller) {

        state = State.Initializing;
        Provider<? extends Command> defaultCommandProvider1 = defaultCommandProvider;
        this.pivotRightMotor = pivotRightMotor;
        this.pivotLeftMotor = pivotLeftMotor;
        this.controller = controller;
        this.leftLimit = leftPivotLimit;
        this.rightLimit = rightPivotLimit;
        configRightEncoder();
        configLeftEncoder();
        setPIDGains();
        setMaxVoltage();
        pivotRightMotor.setInverted(true);
        logger.info("Initialized And Configured IntakePivotSubsystem");
    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    private void setMaxVoltage() {
        pivotRightMotor.configPeakOutputForward(Intake.MAX_PERCENT_OUT, 10);
        pivotRightMotor.configPeakOutputReverse(-Intake.MAX_PERCENT_OUT, 10);
        pivotLeftMotor.configPeakOutputForward(Intake.MAX_PERCENT_OUT, 10);
        pivotLeftMotor.configPeakOutputReverse(-Intake.MAX_PERCENT_OUT, 10);
    }

    public void zeroLeftEncoder() {
        pivotLeftMotor.setSelectedSensorPosition(0, 0, 10);
    }

    public void zeroRightEncoder() {
        pivotRightMotor.setSelectedSensorPosition(0, 0, 10);
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_P_GAIN, Defaults.INTAKE_P_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_I_GAIN, Defaults.INTAKE_I_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.INTAKE_D_GAIN, Defaults.INTAKE_D_GAIN);
        SmartDashboard.putString(Intake.NAME + " Pivot State", state.toString());
    }

    @Override
    public void readSmartDashboard() {
        pgain = SmartDashboard.getNumber(DashboardKeys.INTAKE_P_GAIN, Defaults.INTAKE_P_GAIN);
        igain = SmartDashboard.getNumber(DashboardKeys.INTAKE_I_GAIN, Defaults.INTAKE_I_GAIN);
        dgain = SmartDashboard.getNumber(DashboardKeys.INTAKE_D_GAIN, Defaults.INTAKE_D_GAIN);
    }

    public void setPIDGains() {
        pivotRightMotor.config_kP(0, pgain, 0);
        pivotRightMotor.config_kI(0, igain, 0);
        pivotRightMotor.config_kD(0, dgain, 0);

        pivotLeftMotor.config_kP(0, pgain, 0);
        pivotLeftMotor.config_kI(0, igain, 0);
        pivotLeftMotor.config_kD(0, dgain, 0);
    }

    public void setZeroingPIDGains() {
        double pgainZeroing = Defaults.INTAKE_P_GAIN_ZEROING;
        pivotRightMotor.config_kP(0, pgainZeroing, 0);
        double igainZeroing = Defaults.INTAKE_I_GAIN_ZEROING;
        pivotRightMotor.config_kI(0, igainZeroing, 0);
        double dgainZeroing = Defaults.INTAKE_D_GAIN_ZEROING;
        pivotRightMotor.config_kD(0, dgainZeroing, 0);

        pivotLeftMotor.config_kP(0, pgainZeroing, 0);
        pivotLeftMotor.config_kI(0, igainZeroing, 0);
        pivotLeftMotor.config_kD(0, dgainZeroing, 0);
    }

    public void configLeftEncoder() {
        pivotLeftMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        pivotLeftMotor.setSensorPhase(false);
        pivotLeftMotor.setInverted(false);
    }

    public void configRightEncoder() {
        pivotRightMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        pivotRightMotor.setSensorPhase(false);
        pivotLeftMotor.setInverted(false);
    }

    public double angleOfJoystick(int joystickXAxis, int joystickYAxis) {
        double xAxis = controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        if (((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35 >= 215)
            return 215.;
        else if (((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35 <= 70)
            return 70.;
        else
            return ((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35;
    }

    public double angleOfRightJoystick(int joystickXAxis, int joystickYAxis) {
        double xAxis = -controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        if (((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35 >= 215)
            return 215.;
        else if (((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35 <= 70)
            return 70.;
        else
            return ((Math.atan(-yAxis / xAxis) * 180) / Math.PI) + 90. + 35;
    }

    private void positionalControl(TalonSRX talon, int joystickXAxis, int joystickYAxis) {
        double angleOfJoystick;
        boolean isJoystickActive = isJoystickActive(joystickXAxis, joystickYAxis);
        if (talon.equals(pivotRightMotor)) {
            angleOfJoystick = angleOfRightJoystick(joystickXAxis, joystickYAxis);
            isJoystickActive = isRightJoystickActive(joystickXAxis, joystickYAxis);
        } else {
            angleOfJoystick = angleOfJoystick(joystickXAxis, joystickYAxis);
            isJoystickActive = isJoystickActive(joystickXAxis, joystickYAxis);
        }

        if (angleOfJoystick >= 0 && isJoystickActive) {
            state = State.Moving;
            double position = ((angleOfJoystick / 360) * Intake.ENCODER_EDGES_PER_REVOLUTION) * (Intake.PIVOT_GEAR_RATIO);
            talon.set(ControlMode.Position, position);
            double holdPosition = talon.getSelectedSensorPosition(0);
            logger.debug("Intake Position: [{}]. Actual: [{}]", position, holdPosition);
        } else {
            state = State.Holding;
            talon.set(ControlMode.Position, talon.getSelectedSensorPosition(0));
        }
    }

    private double lastSetPoint = 0;

    public void presetRightIntakePivot(double angle, boolean isZeroingPID) {
        if (isZeroingPID) {
            setZeroingPIDGains();
        } else {
            setPIDGains();
        }
        double position = ((angle / 360) * Intake.ENCODER_EDGES_PER_REVOLUTION) * (Intake.PIVOT_GEAR_RATIO);
        if (getRightLimit() && angle < 0) {
            pivotRightMotor.set(ControlMode.Disabled, 0);
        } else if (lastSetPoint > 150) {
            pivotRightMotor.set(ControlMode.Position, position);
            pivotLeftMotor.set(ControlMode.Position, position);
        } else {
            pivotRightMotor.set(ControlMode.Position, position);
        }
        lastSetPoint = angle;
    }


    int leftSetpoint = 0;

    public void presetLeftIntakePivot(double angle, boolean isZeroingPID) {
        if (isZeroingPID) {
            setZeroingPIDGains();
        } else {
            setPIDGains();
        }
        int position = (int) (((angle / 360) * Intake.ENCODER_EDGES_PER_REVOLUTION) * (Intake.PIVOT_GEAR_RATIO));
        leftSetpoint = position;
        if (getLeftLimit() && angle < 0) {
            pivotLeftMotor.set(ControlMode.Disabled, 0);
        } else {
            pivotLeftMotor.set(ControlMode.Position, position);
        }
    }


    public void controlLeftPivot() {
        int joystickXAxis = 0;
        int joystickYAxis = 1;
        positionalControl(pivotLeftMotor, joystickXAxis, joystickYAxis);
    }

    public void controlRightPivot() {
        int joystickXAxis = 4;
        int joystickYAxis = 5;
        positionalControl(pivotRightMotor, joystickXAxis, joystickYAxis);
    }

    public boolean isJoystickActive(int joystickXAxis, int joystickYAxis) {
        double xAxis = controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        return Math.pow(xAxis, 2) + Math.pow(yAxis, 2) >= .5 && xAxis >= 0;
    }

    public boolean isRightJoystickActive(int joystickXAxis, int joystickYAxis) {
        double xAxis = controller.getRawAxis(joystickXAxis);
        double yAxis = controller.getRawAxis(joystickYAxis);
        return Math.pow(xAxis, 2) + Math.pow(yAxis, 2) >= .5 && xAxis < 0;
    }

    @Override
    protected void initDefaultCommand() {
    }

    public double getPosition() {
        return pivotRightMotor.getSelectedSensorPosition(0);
    }

    public void stopRightPivot() {
        pivotRightMotor.set(ControlMode.PercentOutput, 0);
    }

    public void stopLeftPivot() {
        pivotLeftMotor.set(ControlMode.PercentOutput, 0);
    }

    public boolean getLeftLimit() {
        return !leftLimit.get();
    }

    public boolean getRightLimit() {
        return !rightLimit.get();
    }

    public boolean hasReachedPosition(double angle) {
        double position = ((angle / 360) * Intake.ENCODER_EDGES_PER_REVOLUTION) * (Intake.PIVOT_GEAR_RATIO);
        return isleftAtPosition(position) && isRightAtPosition(position);
    }

    private boolean isleftAtPosition(double position) {
        return position < pivotLeftMotor.getSelectedSensorPosition(0) + 500
                && position > pivotLeftMotor.getSelectedSensorPosition(0) - 500;
    }

    private boolean isRightAtPosition(double position) {
        return position < pivotRightMotor.getSelectedSensorPosition(0) + 500
                && position > pivotRightMotor.getSelectedSensorPosition(0) - 500;
    }

    private double getAngleLeftSide() {
        return (360 * ((pivotLeftMotor.getSelectedSensorPosition(0) / Intake.ENCODER_EDGES_PER_REVOLUTION))
                / (Intake.PIVOT_GEAR_RATIO));
    }

    private double ticsToDegrees(int tics) {
        return (360 * ((pivotLeftMotor.getSelectedSensorPosition(0) / Intake.ENCODER_EDGES_PER_REVOLUTION))
                / (Intake.PIVOT_GEAR_RATIO));
    }

    private double getAngleRightSide() {
        return (360 * ((pivotRightMotor.getSelectedSensorPosition(0) / Intake.ENCODER_EDGES_PER_REVOLUTION))
                / (Intake.PIVOT_GEAR_RATIO));
    }

    public boolean isSafeForElevator() {
        return getAngleLeftSide() > 45 && getAngleRightSide() > 45;
    }
}
