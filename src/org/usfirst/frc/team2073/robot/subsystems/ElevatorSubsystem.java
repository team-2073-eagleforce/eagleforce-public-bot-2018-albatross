package org.usfirst.frc.team2073.robot.subsystems;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.team2073.common.annotations.Development;
import com.team2073.common.motionprofiling.MotionProfileGenerator;
import com.team2073.common.motionprofiling.MotionProfileHelper;
import com.team2073.common.motionprofiling.TrajectoryPointPusher;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.speedcontrollers.EagleSPX;
import com.team2073.common.speedcontrollers.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.elevator.ElevatorStopMotorsCommand;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Defaults;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Elevator;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Shooter;
import org.usfirst.frc.team2073.robot.domain.MotionProfileConfiguration;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;
import org.usfirst.frc.team2073.robot.util.EagleTimer;
import org.usfirst.frc.team2073.robot.util.inject.InjectNamed;

import javax.annotation.PostConstruct;
import java.util.List;

@Singleton
public class ElevatorSubsystem extends Subsystem implements SmartDashboardAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private Provider<ElevatorStopMotorsCommand> defaultCommandProvider;
    @Inject
    private ShooterPivotSubsystem shooterPivotSys;
    @Inject
    private IntakePivotSubsystem intakeSys;
    @InjectNamed
    private EagleSRX elevatorMaster;
    @InjectNamed
    private EagleSPX elevatorSlave;
    @InjectNamed
    private Solenoid elevatorBrake;
    @InjectNamed
    private DigitalInput elevatorMax;
    @InjectNamed
    private DigitalInput elevatorMin;

    // Inner class helpers
    private Configurator configure;
    private InputGateway in;
    private OutputGateway out;
    private MotionProfilingGateway mp;
    private Util util;
    private Dev dev;

    private EagleTimer brakeTimer = new EagleTimer();

    private Command shooterPivotIntake;
    private Command shooterPivotFrontFlat;
    private Command shooterPivotBackFlat;
    private Command shooterPivotShootFront;
    private Command shooterPivotShootBack;
    private Command shooterPivotLowShoot;

    private State elevatorState = State.INITIALIZING;
    private Position position = Position.STARTING_CONFIGURATION;
    private BikeBreak bikeBreak = BikeBreak.ENGAGED;

    @PostConstruct
    public void init(CommandFactory commandFactory) {
        shooterPivotIntake = commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.INTAKE);
        shooterPivotFrontFlat = commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.FRONT_FLAT);
        shooterPivotShootFront = commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.FRONT_SHOOT);
        shooterPivotShootBack = commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.BACK_SHOOT);
        shooterPivotLowShoot = commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.LOW_SCALE);
        initHelpers();
        elevatorMaster.setInverted(Elevator.MASTER_DEFAULT_DIRECTION);
        elevatorSlave.setInverted(Elevator.SLAVE_DEFAULT_DIRECTION);
        configure.configEncoder();
        configure.setPIDGains();
        out.enableCoastMode();
        elevatorMaster.configAllowableClosedloopError(0, 0, 10);
        configure.setSlave();
    }

    private void initHelpers() {
        configure = new Configurator();
        in = new InputGateway();
        out = new OutputGateway();
        mp = new MotionProfilingGateway();
        LogHelper log = new LogHelper();
        util = new Util();
        dev = new Dev();
    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(defaultCommandProvider.get());
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.setDefaultNumber(Elevator.F_GAIN_KEY, Elevator.F_GAIN);
        SmartDashboard.setDefaultNumber(Elevator.P_GAIN_KEY, Elevator.P_GAIN_DOWN);
        SmartDashboard.setDefaultNumber(Elevator.I_GAIN_KEY, Elevator.I_GAIN_DOWN);
        SmartDashboard.setDefaultNumber(Elevator.D_GAIN_KEY, Elevator.D_GAIN_DOWN);
        SmartDashboard.putString(Elevator.NAME + " State", elevatorState.toString());
        SmartDashboard.putString(Elevator.NAME + " Position", position.toString());
        SmartDashboard.putBoolean("ElevatorMax Sensor", in.atMax());
        SmartDashboard.putBoolean("ElevatorMin Sensor", in.atMin());
        LiveWindow.add(this);
    }

    @Override
    public void readSmartDashboard() {
        configure.fgain = SmartDashboard.getNumber(Elevator.F_GAIN_KEY, Elevator.F_GAIN);
        configure.pgainDown = SmartDashboard.getNumber(Elevator.P_GAIN_KEY, Elevator.P_GAIN_DOWN);
        configure.igainDown = SmartDashboard.getNumber(Elevator.I_GAIN_KEY, Elevator.I_GAIN_DOWN);
        configure.dgainDown = SmartDashboard.getNumber(Elevator.D_GAIN_KEY, Elevator.D_GAIN_DOWN);
    }

    private boolean hasSetSlave = false;

    @Override
    public void periodic() {
        if (!hasSetSlave) {
            configure.setSlave();
            hasSetSlave = true;
        }
    }

    // Public API Approved methods
    // ================================================================================

    public double getCurrentHeight() {
        return in.getCurrentHeight();
    }

    /**
     * State manipulating method (public API approved).
     */
    public void elevatorSetPoint(double height) {
        out.elevatorSetPoint(height);
    }

    /**
     * State manipulating method (public API approved).
     */
    public void maxEncoder() {
        out.maxEncoder();
    }

    /**
     * State manipulating method (public API approved).
     */
    public void zeroEncoder() {
        out.zeroEncoder();
    }

    /**
     * State manipulating method (public API approved).
     */
    public void goToMax() {
        mp.goToMax();
    }

    /**
     * State manipulating method (public API approved).
     */
    public void goToReceive() {
        mp.goToReceive();
    }

    /**
     * State manipulating method (public API approved).
     */
    public void fromTopToPivotHeight() {
        mp.fromTopToPivotHeight();
    }

    /**
     * State manipulating method (public API approved).
     */
    public void receiveToSwitch() {
        mp.receiveToSwitch();
    }

    // ================================================================================

    /**
     * @deprecated This will be handled by the CommandExecution object
     */
    public boolean hasReachedPosition(double height) {
        return in.hasReachedPosition(height);
    }

    /**
     * @deprecated This will be handled by the CommandExecution object
     */
    public boolean isMotionProfilingFinished() {
        return mp.isMotionProfilingFinished();
    }

    /**
     * @deprecated This will be handled by the CommandExecution object
     */
    public void stopMotionProfiling() {
        mp.stopMotionProfiling();
    }

    // ================================================================================

    /**
     * @deprecated Change this to be handled internally. Subsystems should be
     * responsible for verifying it didn't go over the motion profile's
     * end point, not each command that calls it.
     */
    @Deprecated
    public boolean isAtTop() {
        return in.isAtTop();
    }

    /**
     * @deprecated See {@link #isAtTop()} javadocs
     */
    @Deprecated
    public boolean isAtBottom() {
        return in.isAtBottom();
    }

    /**
     * @deprecated This should be called automatically by the elevator subsystem
     */
    public void enableCoastMode() {
        out.enableCoastMode();
    }

    /**
     * @deprecated This should be called automatically by the elevator subsystem
     */
    public void releaseBrake() {
        out.releaseBrake();
    }

    /**
     * @deprecated This should be called automatically by the elevator subsystem
     */
    public void brakeElevator() {
        out.brakeElevator();
    }

    /**
     * @deprecated This should be called automatically by the elevator subsystem when
     * state == PID
     */
    public void moveToSetPoint() {
        out.moveToSetPoint();
    }

    /**
     * @deprecated This should be called automatically by the elevator subsystem
     */
    public void stopMotors() {
        out.stopMotors();
    }

    /**
     * @deprecated Motion profiling should be processed automatically in subsystem#periodic() when
     * the mode is MotionProfiling
     */
    public void processMotionProfiling() {
        mp.processMotionProfiling();
    }

    /**
     * <h1>ONLY USE THIS FOR DEVELOPMENT</h1>
     * <p>
     * Returns access to the inner {@link Dev} class.
     *
     * @return
     */
    @Deprecated
    @Development
    public Dev getDev() {
        return dev;
    }

    private long releaseBreakPoint = BREAK_NOT_RELEASED;
    private static final long BREAK_NOT_RELEASED = -2;
    private static final long BREAK_RELEASE_COMPLETE = -1;
    private static final long BREAK_RELEASE_DELAY = 110;


    enum State {
        INITIALIZING, MOVING, OVERRIDE, HOLDING, AT_TOP_LIMIT, AT_BOTTOM_LIMIT, ZEROING, ERROR, ZEROED
    }

    enum Position {
        MOVING_TO_MAX, MOVING_TO_SWITCH, MOVING_TO_RECEIVE, AT_MAX, AT_SWITCH, AT_RECEIVE, STARTING_CONFIGURATION,
    }

    enum BikeBreak {
        ENGAGED, DISENGAGED
    }

    /**
     * Manages setting configuration on various components. Generally this is one time configuration
     * or configuration only changed during development (such as through the smartdashboard).
     */
    private class Configurator {

        private double fgain = Elevator.F_GAIN;
        private double pgainDown = Elevator.P_GAIN_DOWN;
        private double igainDown = Elevator.I_GAIN_DOWN;
        private double dgainDown = Elevator.D_GAIN_DOWN;

        private double pgainUp = Elevator.P_GAIN_UP;
        private double igainUp = Elevator.I_GAIN_UP;
        private double dgainUp = Elevator.D_GAIN_UP;

        private void configEncoder() {
            elevatorMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        }

        private void setSlave() {
            elevatorSlave.follow(elevatorMaster);
        }

        private void setPIDGains() {
            elevatorMaster.config_kP(0, pgainUp, 0);
            elevatorSlave.config_kP(0, pgainUp, 0);

            elevatorMaster.config_kI(0, igainUp, 0);
            elevatorSlave.config_kI(0, igainUp, 0);

            elevatorMaster.config_kD(0, dgainUp, 0);
            elevatorSlave.config_kD(0, dgainUp, 0);

            elevatorMaster.config_kP(1, pgainDown, 0);
            elevatorSlave.config_kP(1, pgainDown, 0);

            elevatorMaster.config_kI(1, igainDown, 0);
            elevatorSlave.config_kI(1, igainDown, 0);

            elevatorMaster.config_kD(1, dgainDown, 0);
            elevatorSlave.config_kD(1, dgainDown, 0);
        }
    }

    private enum Action {
        WAITING, PID, MOTION_PROFILING
    }

    /**
     * All component input (motors, sensors, etc.) should be ran through this
     * class. The only thing this class should modify is internal state
     * variables. It should only observe motors, not output to them.
     */
    private class InputGateway {

        public boolean hasReachedPositionTics(double encoderPosition) {
            return util.aboutEquals(elevatorMaster.getSelectedSensorPosition(0), encoderPosition);
        }

        public boolean hasReachedPosition(double height) {
            double encoderPosition = height * Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL;
            return util.aboutEquals(elevatorMaster.getSelectedSensorPosition(0), encoderPosition);
        }

        public double getCurrentHeightTics() {
            return elevatorMaster.getSelectedSensorPosition(0);
        }

        public double getCurrentHeight() {
            return elevatorMaster.getSelectedSensorPosition(0) / Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL;
        }

        private boolean isPositionSafe(double position) {
            return (!(position < Elevator.BOTTOM_HEIGHT) && !(position > Elevator.MAX_TRAVEL)) || bikeBreak == BikeBreak.ENGAGED;
        }

        private boolean breakReleaseWaitComplete() {
            if (releaseBreakPoint == BREAK_RELEASE_COMPLETE || releaseBreakPoint == BREAK_NOT_RELEASED) {
                System.out.println("Break release wait inactive");
                return true;
            }

            long currTime = System.currentTimeMillis();

            if (releaseBreakPoint + BREAK_RELEASE_DELAY < currTime) {
                System.out.printf("Break release complete. Current time: [%s] Diff: [%s] Release [%s + %s = %s]\n"
                        , currTime, releaseBreakPoint + BREAK_RELEASE_DELAY - currTime, releaseBreakPoint, BREAK_RELEASE_DELAY, releaseBreakPoint + BREAK_RELEASE_DELAY);
                releaseBreakPoint = BREAK_RELEASE_COMPLETE;
                return true;
            } else {
                return false;
            }
        }

        private double getMotorVoltage() {
            return elevatorMaster.getMotorOutputVoltage();
        }

        private void checkIfMoving() {
            if (elevatorMaster.getMotorOutputVoltage() > .01 || elevatorMaster.getMotorOutputVoltage() < -.01) {
                elevatorState = State.MOVING;
            }
        }

        public boolean isAtBottom() {
            return elevatorState == State.AT_BOTTOM_LIMIT;
        }

        public boolean isAtTop() {
            return elevatorState == State.AT_TOP_LIMIT;
        }

        private boolean isElevatorSafe(double output) {
            configure.setPIDGains();
            if (isAtBottom() && output < 0) {
                logger.trace("Elevator Trying To Go Past Bottom");
                return false;
            } else if (isAtTop() && output > 0) {
                logger.trace("Elevator Trying To Go Past Top");
                return false;
            }
            return true;
        }

        public boolean atMax() {
            return !elevatorMax.get();
        }

        public boolean reachedSensorPastStartingPosition(boolean isTravelingUp) {
            if (isTravelingUp)
                return atMax();
            else
                return atMin();
        }

        public boolean isTravelingUp(double setPoint) {
            return setPoint > getCurrentHeightTics();
        }

        public boolean atMin() {
            return !elevatorMin.get();
        }

        @Deprecated
        public double getPosition() {
            return elevatorMaster.getSelectedSensorPosition(0) / Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL;
        }

    }

    /**
     * Manages all output to motors. All output should run through this class (besides motion profiling
     * which uses {@link MotionProfilingGateway}.
     */
    private class OutputGateway {

        private double targetSetPoint;

        public void zeroEncoder() {
            elevatorState = State.AT_BOTTOM_LIMIT;
            enableBrakeMode();
            elevatorMaster.setSelectedSensorPosition(0, 0, 0);
        }

        public void maxEncoder() {
            elevatorState = State.AT_TOP_LIMIT;
            enableBrakeMode();
            elevatorMaster.setSelectedSensorPosition(
                    (int) Math.round(Elevator.MAX_TRAVEL * Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL), 0, 10);
        }

        public void enableBrakeMode() {
            elevatorMaster.setNeutralMode(NeutralMode.Brake);
            elevatorSlave.setNeutralMode(NeutralMode.Brake);
        }

        public void enableCoastMode() {
            elevatorMaster.setNeutralMode(NeutralMode.Coast);
            elevatorSlave.setNeutralMode(NeutralMode.Coast);
        }

        public void brakeElevator() {
            elevatorBrake.set(false);
            elevatorState = State.HOLDING;
            bikeBreak = BikeBreak.ENGAGED;
        }

        public void releaseBrake() {
            elevatorBrake.set(true);
            bikeBreak = BikeBreak.DISENGAGED;
        }

        public boolean isBrakeEngaged() {
            return bikeBreak == BikeBreak.ENGAGED;
        }

        public void overrideElevator(double speed) {
            elevatorMaster.set(ControlMode.PercentOutput, speed);
            elevatorState = State.OVERRIDE;
        }

        public void dissableMode() {
            mp.stopMotionProfiling();
            elevatorMaster.set(ControlMode.Disabled, 0);
            enableCoastMode();
            brakeElevator();
        }

        public void stopMotors() {
            elevatorMaster.set(ControlMode.PercentOutput, 0);
        }

        private void moveElevator(ControlMode mode, double value) {
            if (!in.isElevatorSafe(value)) {
                elevatorMaster.set(mode, 0);
            } else {
                elevatorMaster.set(mode, value);
            }
            in.checkIfMoving();
        }

        public void moveToLimit() {
            elevatorState = State.ZEROING;
            if (!in.atMax() && !in.atMin()) {
                hitLimitSwitch();
                logger.trace("Zeroing the elevator");
            } else if (in.isAtBottom() && in.getMotorVoltage() < 0) {

            }
        }

        private void hitLimitSwitch() {
            if (in.isAtBottom()) {
                elevatorMaster.set(ControlMode.PercentOutput, -.1);
            } else if (in.isAtTop()) {
                elevatorMaster.set(ControlMode.PercentOutput, .1);
            }
        }

        /**
         * This method sets the target set point, enables coast mode, and angles the
         * shooter. It also checks if the height is in a safe position, and if it is, it
         * configures the elevator. Otherwise, it disables the elevator.
         */
        public void elevatorSetPointTics(double tics) {
            elevatorSetPoint(tics / Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL);
        }

        /**
         * @deprecated Use {@link #elevatorSetPointTics(double)} instead.
         */
        public void elevatorSetPoint(double height) {
            targetSetPoint = height * Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL;
            enableCoastMode();
            angleShooter(height);
            if (in.isPositionSafe(height)) {
                if (height < in.getCurrentHeight()) {
                    elevatorMaster.selectProfileSlot(1, 0);
                    elevatorMaster.configPeakOutputForward(.4, 10);
                    elevatorMaster.configPeakOutputReverse(-.4, 10);
                } else {
                    elevatorMaster.selectProfileSlot(0, 0);
                    elevatorMaster.configPeakOutputForward(.95, 10);
                    elevatorMaster.configPeakOutputReverse(-.95, 10);
                }
            } else {
                elevatorMaster.set(ControlMode.Disabled, 0);
                logger.warn("Cannot go to position [{}]", height);
            }
        }

        /**
         * If the intake pivot is in a safe position and the elevator is at a safe
         * height, the brake will be released if it is not already. Once the brake
         * release delay has passed, the elevator set point will be set. If at any point
         * the brake is engaged again, the elevator will be disabled.
         */
        public void moveToSetPoint() {
            if ((bikeBreak == BikeBreak.ENGAGED
                    && (intakeSys.isSafeForElevator() || (RobotState.isAutonomous()
                    && targetSetPoint > in.getCurrentHeight() / Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL))) && !shooterPivotSys.isPivotBack()) {
                releaseBrake();
                releaseBreakPoint = System.currentTimeMillis();
            }
            if (bikeBreak != BikeBreak.ENGAGED && in.breakReleaseWaitComplete() && !shooterPivotSys.isPivotBack() /*&& !hasReachedPosition(targetSetPoint/Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL)*/) {
                elevatorMaster.set(ControlMode.Position, targetSetPoint);
            } else {
                elevatorMaster.set(ControlMode.Disabled, 0);
            }

        }

        /**
         * This method checks the Set Point and angles Shooter Pivot based on it.
         * <p>
         * <ul>
         * <li>If the Set Point is below Pivot Height, the Set Point will be set again to Pivot Height.
         * <li>If the Set Point is Switch Height, Shooter Pivot will be angled at Front Flat.
         * <li>If the Set Point is Bottom Height, Shooter Pivot will be angled at Intake.
         * <li>If the Set Point is Max Travel, Shooter Pivot will be angled at Shoot (which is based on whether it's currently angled at Front or Back).
         * <li>If the Set Point is Pivot Height, nothing will happen because that section of code is commented out.
         * <li>Otherwise, the logger will print warning.
         * </ul>
         */
        private void angleShooter(double heightSetpoint) {
            if (heightSetpoint == Elevator.SWITCH_HEIGHT) {
                shooterPivotFrontFlat.start();

            } else if (heightSetpoint == Elevator.BOTTOM_HEIGHT) {
                shooterPivotIntake.start();

            } else if (heightSetpoint == Elevator.MAX_TRAVEL) {
                shooterPivotShootFront.start();
//				}

            } else if (heightSetpoint == Elevator.LOW_SCALE) {
                shooterPivotLowShoot.start();
            } else if (heightSetpoint == Elevator.PIVOT_HEIGHT) {
                shooterPivotShootBack.start();
            } else {
                logger.warn("Invalid Setpoint [{}]", heightSetpoint);
            }
        }
    }


    private class MotionProfilingGateway {

        private boolean reachedMinPoints = false;

        private List<TrajectoryPoint> receiveToMaxList;
        private List<TrajectoryPoint> receiveToSwitchList;
        private List<TrajectoryPoint> switchToMaxList;

        private boolean isProfilingUp;
        private MotionProfileHelper elevatorMotorHelper;
        private TrajectoryPointPusher trajPointPusher;

        public MotionProfilingGateway() {
            elevatorMotorHelper = new MotionProfileHelper(elevatorMaster, Elevator.MASTER_DEFAULT_DIRECTION,
                    Elevator.F_GAIN_KEY, Elevator.F_GAIN, "Elevator");
        }

        public void processMotionProfiling() {
            processMotionProfiling(isProfilingUp);
        }

        private void processMotionProfiling(boolean isUp) {
            double value = 0;
            if (isUp) {
                value = 1;
            } else if (!isUp) {
                value = -1;
            }

            if (elevatorMotorHelper.isBufferSufficentlyFull(Defaults.MINIMUM_POINTS_TO_RUN)) {
                reachedMinPoints = true;
            }

            if (reachedMinPoints && in.isElevatorSafe(value)) {
                elevatorMotorHelper.processPoints();
                elevatorState = State.MOVING;

                if (logger.isTraceEnabled()) {
                    logger.trace("Error: [{}] \t Speed: [{}] \t EncPos: [{}] \t Voltage: [{}]"
                            , elevatorMaster.getClosedLoopError(0) //
                            , elevatorMaster.getSelectedSensorVelocity(0) //
                            , elevatorMaster.getSelectedSensorPosition(0) //
                            , elevatorMaster.getMotorOutputVoltage()
                    );
                }
            }

        }

        public void stopMotionProfiling() {
            if (trajPointPusher != null) {
                trajPointPusher.interrupt();
            }
            elevatorMotorHelper.stopTalon();
            reachedMinPoints = false;
            elevatorMaster.clearMotionProfileHasUnderrun(0);
            elevatorMaster.clearStickyFaults(0);
        }

        public boolean isMotionProfilingFinished() {
            return reachedMinPoints && elevatorMotorHelper.isFinished();
        }

        public void resetMotionProfiling(MotionProfileConfiguration config, boolean forwards) {
            elevatorMotorHelper.reset();
            List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
            trajPointPusher = new TrajectoryPointPusher(elevatorMotorHelper,
                    trajPointList);
            new Thread(trajPointPusher).start();

            elevatorMotorHelper.checkDirection(forwards);
        }

        public void resetMotionProfiling(List<TrajectoryPoint> trajPointList, boolean forwards) {
            elevatorMotorHelper.reset();
            trajPointPusher = new TrajectoryPointPusher(elevatorMotorHelper,
                    trajPointList);
            new Thread(trajPointPusher).start();
            elevatorMotorHelper.checkDirection(forwards);

        }

        public MotionProfileConfiguration elevatorMoveConfiguration(double linearDistance) {
            MotionProfileConfiguration configuration = new MotionProfileConfiguration();
            double encoderTics = linearDistance * Elevator.ENCODER_EDGES_PER_INCH_OF_TRAVEL;
            configuration.setEndDistance(encoderTics);
            configuration.setIntervalVal(10);
            configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
            configuration.setMaxVel(Elevator.MAX_VELOCITY);
            configuration.setMaxAcc(Elevator.MAX_ACCELERATION);
            configuration.setVelocityOnly(false);
            return configuration;
        }

        public void receiveToMax() {
            isProfilingUp = true;
            resetMotionProfiling(receiveToMaxList, false);
            position = Position.MOVING_TO_MAX;
        }

        public void receiveToSwitch() {
            isProfilingUp = true;
            resetMotionProfiling(receiveToSwitchList, false);
            position = Position.MOVING_TO_SWITCH;
        }

        private void switchToMax() {
            isProfilingUp = true;
            resetMotionProfiling(switchToMaxList, false);
            position = Position.MOVING_TO_MAX;
        }

        private void switchToReceive() {
            isProfilingUp = false;
            resetMotionProfiling(receiveToSwitchList, true);
            position = Position.MOVING_TO_RECEIVE;
        }

        private void maxToSwitch() {
            isProfilingUp = false;
            resetMotionProfiling(switchToMaxList, true);
            position = Position.MOVING_TO_SWITCH;
        }

        private void maxToReceive() {
            isProfilingUp = false;
            resetMotionProfiling(receiveToMaxList, false);
            position = Position.MOVING_TO_RECEIVE;
        }

        public void goToMax() {
            if (util.aboutEquals(in.getPosition(), Elevator.SWITCH_HEIGHT)) {
                switchToMax();
            } else if (util.aboutEquals(in.getPosition(), 0)) {
                receiveToMax();
            } else if (util.aboutEquals(in.getPosition(), Elevator.MAX_TRAVEL)) {
                position = Position.AT_MAX;
            } else {
                elevatorState = State.ERROR;
            }
        }

        public void goToSwitch() {
            if (util.aboutEquals(in.getPosition(), Elevator.MAX_TRAVEL)) {
                maxToSwitch();
            } else if (util.aboutEquals(in.getPosition(), 0)) {
                receiveToSwitch();
            } else if (util.aboutEquals(in.getPosition(), Elevator.SWITCH_HEIGHT)) {
                position = Position.AT_SWITCH;
            } else {
                elevatorState = State.ERROR;
            }
        }

        public void goToReceive() {
            if (util.aboutEquals(in.getPosition(), Elevator.SWITCH_HEIGHT)) {
                switchToReceive();
            } else if (util.aboutEquals(in.getPosition(), Elevator.MAX_TRAVEL)) {
                maxToReceive();
            } else if (util.aboutEquals(in.getPosition(), 0)) {
                position = Position.AT_RECEIVE;
            } else {
                elevatorState = State.ERROR;
            }
        }

        public void fromTopToPivotHeight() {
            switchToReceive();
        }

    }

    private static class Util {

        private boolean aboutEquals(double val1, double val2) {
            return val1 >= val2 - 300 && val1 <= val2 + 300;
        }
    }

    /**
     * Handles complex logging statements that would add too much noise to the code.
     */
    private class LogHelper {

        public void printSlaveVoltage() {
            System.out.println("Slave Voltage: " + elevatorSlave.getMotorOutputVoltage());

        }
    }

    /**
     * Methods only needed for development purposes
     */
    public class Dev {

        private Dev() {
            // private constructor
        }

        @Development
        public void masterOnly() {
            elevatorMaster.set(ControlMode.PercentOutput, .5);
        }

        @Development
        public void slaveOnly() {
            elevatorSlave.set(ControlMode.PercentOutput, .5);
        }

        @Development
        public void elevatorUp() {
            out.moveElevator(ControlMode.PercentOutput, 1);
            System.out.println("Speed: " + elevatorMaster.getSelectedSensorVelocity(0));
        }

        @Development
        public void elevatorDown() {
            out.moveElevator(ControlMode.PercentOutput, -1);
        }

    }

    public void endSequence() {
        if (util.aboutEquals(getCurrentHeight(), Elevator.MAX_TRAVEL)) {
            maxElevator();
        } else if (util.aboutEquals(getCurrentHeight(), Elevator.BOTTOM_HEIGHT)) {
            zeroElevator();
        }
    }

    private boolean maxElevator() {
        if (!isAtTop()) {
            elevatorMaster.set(ControlMode.PercentOutput, .5);
            return false;
        } else
            return true;

    }

    private boolean zeroElevator() {
        if (!isAtBottom()) {
            elevatorMaster.set(ControlMode.PercentOutput, -.5);
            return false;
        } else
            return true;

    }

    public boolean hasCompletedSetpoint() {
        return false;
    }
}
