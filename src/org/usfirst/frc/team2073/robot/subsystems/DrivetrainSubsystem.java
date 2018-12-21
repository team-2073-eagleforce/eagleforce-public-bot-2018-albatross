package org.usfirst.frc.team2073.robot.subsystems;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motion.TrajectoryPoint.TrajectoryDuration;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.team2073.common.motionprofiling.MotionProfileGenerator;
import com.team2073.common.motionprofiling.MotionProfileHelper;
import com.team2073.common.motionprofiling.TrajectoryPointPusher;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.speedcontrollers.EagleSPX;
import com.team2073.common.speedcontrollers.EagleSRX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.drive.DriveWithJoystickCommand;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Camera;
import org.usfirst.frc.team2073.robot.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Defaults;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Drivetrain;
import org.usfirst.frc.team2073.robot.domain.MotionProfileConfiguration;
import org.usfirst.frc.team2073.robot.util.CustomPIDService;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class DrivetrainSubsystem extends Subsystem implements SmartDashboardAware {
    public static final double DEFAULT_INVERSE = .3;
    public static final double DEFAULT_SENSE = .2;


    private final Logger logger = LoggerFactory.getLogger(getClass());


    private final Provider<? extends Command> defaultCommandProvider;
    private final EagleSRX leftMotor;
    private final EagleSPX leftMotorSlave;
    private final EagleSRX rightMotor;
    private final EagleSPX rightMotorSlave;
    private final Solenoid solenoid;
    private final PigeonIMU gyro;

    private final MotionProfileHelper leftMotorHelper;
    private final MotionProfileHelper rightMotorHelper;

    private double inverse = DEFAULT_INVERSE;
    private double sense = DEFAULT_INVERSE;

    private double pgain = Defaults.DRIVETRAIN_P_GAIN;
    private double igain = Defaults.DRIVETRAIN_I_GAIN;
    private double dgain = Defaults.DRIVETRAIN_D_GAIN;
    private double fgain = Defaults.DRIVETRAIN_FGAIN;
    private double cameraP = Camera.FOLLOW_P;
    private double cameraI = Camera.FOLLOW_I;
    private double cameraD = Camera.FOLLOW_D;

    private boolean intakeForwards = true;

    private DriveMode mode;
    private Setting setting;
    private boolean reachedMinPoints;

    enum DriveMode {
        Initializing, ManualForward, ManualBackward, MotionProfile, PID
    }

    enum Setting {
        Brake, Coast, TuneF, HighGear, LowGear
    }

    @Inject
    public DrivetrainSubsystem(Provider<DriveWithJoystickCommand> defaultCommandProvider,
                               @Named("leftMotor") EagleSRX leftMotor, @Named("leftMotorSlave") EagleSPX leftMotorSlave,
                               @Named("rightMotor") EagleSRX rightMotor, @Named("rightMotorSlave") EagleSPX rightMotorSlave,
                               @Named("driveSolenoid") Solenoid solenoid, PigeonIMU gyro) {

        mode = DriveMode.Initializing;
        this.defaultCommandProvider = defaultCommandProvider;
        this.leftMotor = leftMotor;
        this.leftMotorSlave = leftMotorSlave;
        this.rightMotor = rightMotor;
        this.rightMotorSlave = rightMotorSlave;
        this.solenoid = solenoid;
        this.gyro = gyro;

        leftMotorHelper = new MotionProfileHelper(leftMotor, Defaults.LEFT_MOTOR_DEFAULT_DIRECTION
                , DashboardKeys.LEFT_DRIVE_F_GAIN, Defaults.LEFT_DRIVE_F_GAIN, "Drive Train Left");
        rightMotorHelper = new MotionProfileHelper(rightMotor, Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION
                , DashboardKeys.RIGHT_DRIVE_F_GAIN, Defaults.RIGHT_DRIVE_F_GAIN, "Drive Train Right");

        configEncoders();
        initTalons();
        enableBrakeMode();
        leftMotor.configAllowableClosedloopError(0, 5, 5);
        leftMotorSlave.configAllowableClosedloopError(0, 5, 5);
        rightMotor.configAllowableClosedloopError(0, 5, 5);
        rightMotorSlave.configAllowableClosedloopError(0, 5, 5);
        defaultTalonDirection();
        zeroEncoders();
        setPIDGains();
        setSlaves();
    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(defaultCommandProvider.get());
    }

    // ============================================================================================================================

    //	Initialization methods
    private void initTalons() {
        leftMotorHelper.initTalon();
        rightMotorHelper.initTalon();
    }


    public void capVoltage() {
        leftMotor.configPeakOutputForward(.9, 0);
        rightMotor.configPeakOutputForward(.9, 0);
        leftMotor.configPeakOutputReverse(-.9, 0);
        rightMotor.configPeakOutputReverse(-.9, 0);
    }

    public void lowCapVoltage() {
        leftMotor.configPeakOutputForward(.45, 0);
        rightMotor.configPeakOutputForward(.45, 0);
        leftMotor.configPeakOutputReverse(-.45, 0);
        rightMotor.configPeakOutputReverse(-.45, 0);
    }

    public void uncapVoltage() {
        leftMotor.configPeakOutputForward(1, 0);
        rightMotor.configPeakOutputForward(1, 0);
        leftMotor.configPeakOutputReverse(-1, 0);
        rightMotor.configPeakOutputReverse(-1, 0);
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.setDefaultNumber(DashboardKeys.DRIVETRAIN_P, Defaults.DRIVETRAIN_P_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.DRIVETRAIN_I, Defaults.DRIVETRAIN_I_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.DRIVETRAIN_D, Defaults.DRIVETRAIN_D_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.LEFT_DRIVE_F_GAIN, Defaults.LEFT_DRIVE_F_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.RIGHT_DRIVE_F_GAIN, Defaults.RIGHT_DRIVE_F_GAIN);
        SmartDashboard.setDefaultNumber(DashboardKeys.CAMERA_P, Camera.FOLLOW_P);
        SmartDashboard.setDefaultNumber(DashboardKeys.CAMERA_I, Camera.FOLLOW_I);
        SmartDashboard.setDefaultNumber(DashboardKeys.CAMERA_D, Camera.FOLLOW_D);
        SmartDashboard.putString(Drivetrain.NAME + " Mode", mode.toString());
        SmartDashboard.putString(Drivetrain.NAME + " Setting", setting.toString());
        SmartDashboard.setDefaultNumber(DashboardKeys.INVERSE, DEFAULT_INVERSE);
        SmartDashboard.setDefaultNumber(DashboardKeys.SENSE, DEFAULT_SENSE);
    }

    @Override
    public void readSmartDashboard() {
        inverse = SmartDashboard.getNumber(DashboardKeys.INVERSE, DEFAULT_INVERSE);
        sense = SmartDashboard.getNumber(DashboardKeys.SENSE, DEFAULT_SENSE);
        pgain = SmartDashboard.getNumber(DashboardKeys.DRIVETRAIN_P, Defaults.DRIVETRAIN_P_GAIN);
        igain = SmartDashboard.getNumber(DashboardKeys.DRIVETRAIN_I, Defaults.DRIVETRAIN_I_GAIN);
        dgain = SmartDashboard.getNumber(DashboardKeys.DRIVETRAIN_D, Defaults.DRIVETRAIN_D_GAIN);
        cameraP = SmartDashboard.getNumber(DashboardKeys.CAMERA_P, Camera.FOLLOW_P);
        cameraI = SmartDashboard.getNumber(DashboardKeys.CAMERA_I, Camera.FOLLOW_I);
        cameraD = SmartDashboard.getNumber(DashboardKeys.CAMERA_D, Camera.FOLLOW_D);
    }

    private void configEncoders() {
        leftMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 5);
        rightMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 5);
        leftMotor.setSensorPhase(true);
        rightMotor.setSensorPhase(true);
    }

    private void defaultTalonDirection() {
        leftMotor.setInverted(Defaults.LEFT_MOTOR_DEFAULT_DIRECTION);
        leftMotorSlave.setInverted(Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
        rightMotor.setInverted(Defaults.RIGHT_MOTOR_DEFAULT_DIRECTION);
        rightMotorSlave.setInverted(Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
    }

    private void setSlaves() {
        leftMotorSlave.follow(leftMotor);
        rightMotorSlave.follow(rightMotor);
    }

    // ============================================================================================================================

    //	Getters & Setters
    public double getGyroAngle() {
        return gyro.getCompassHeading();
    }


    public void setIntakeForwards(boolean intakeForwards) {
        this.intakeForwards = intakeForwards;
    }

    // ============================================================================================================================

    //	PIDF Gains
    public void setPIDGains() {
        leftMotor.config_kP(0, pgain, 0);
        rightMotor.config_kP(0, pgain, 0);
        leftMotorSlave.config_kP(0, pgain, 0);
        rightMotorSlave.config_kP(0, pgain, 0);

        leftMotor.config_kI(0, igain, 0);
        rightMotor.config_kI(0, igain, 0);
        leftMotorSlave.config_kI(0, igain, 0);
        rightMotorSlave.config_kI(0, igain, 0);

        leftMotor.config_kD(0, dgain, 0);
        rightMotor.config_kD(0, dgain, 0);
        leftMotorSlave.config_kD(0, dgain, 0);
        rightMotorSlave.config_kD(0, dgain, 0);

        leftMotor.config_kP(1, cameraP, 0);
        rightMotor.config_kP(1, cameraP, 0);
        leftMotorSlave.config_kP(1, cameraP, 0);
        rightMotorSlave.config_kP(1, cameraP, 0);

        leftMotor.config_kI(1, cameraI, 0);
        rightMotor.config_kI(1, cameraI, 0);
        leftMotorSlave.config_kI(1, cameraI, 0);
        rightMotorSlave.config_kI(1, cameraI, 0);

        leftMotor.config_kD(1, cameraD, 0);
        rightMotor.config_kD(1, cameraD, 0);
        leftMotorSlave.config_kD(1, cameraD, 0);
        rightMotorSlave.config_kD(1, cameraD, 0);

    }

    public void adjustF(double startingGryo) {
        setting = Setting.TuneF;
        if (getGyroAngle() < startingGryo - .2) {
            leftMotorHelper.changeF(.01);
            rightMotorHelper.changeF(-.01);
        } else if (getGyroAngle() > startingGryo + .2) {
            rightMotorHelper.changeF(.01);
            leftMotorHelper.changeF(-.01);
        }
    }

    // ============================================================================================================================

    //	Manual Drive
    public double turnSense(double ptart) {
        return sense * Math.pow(ptart, 3) + ptart * (1 - sense);
    }

    public double inverse(double start) {
        return (start) * inverse + start;
    }


    public void move(double speed, double turn) {
        double rightSide = -(inverse(speed) - (inverse(speed) * turnSense(turn)));
        double leftSide = (inverse(speed) + (inverse(speed) * turnSense(turn)));
        if (intakeForwards) {
            mode = DriveMode.ManualForward;
            rightMotor.set(ControlMode.PercentOutput, rightSide);
            leftMotor.set(ControlMode.PercentOutput, leftSide);
        } else {
            mode = DriveMode.ManualBackward;
            leftMotor.set(ControlMode.PercentOutput, rightSide);
            rightMotor.set(ControlMode.PercentOutput, leftSide);
        }
    }

    public void pointTurn(double turn) {
        rightMotor.set(ControlMode.PercentOutput, -turn);
        leftMotor.set(ControlMode.PercentOutput, -turn);
    }

    // ============================================================================================================================

    //	Sensor Control
    public void zeroEncoders() {
        leftMotor.setSelectedSensorPosition(0, 0, 0);
        rightMotor.setSelectedSensorPosition(0, 0, 0);
    }

    @Override
    public void periodic() {
    }


    // ============================================================================================================================

//	Drivetrain mode Control

    public void shiftHighGear() {
        setting = Setting.HighGear;
        solenoid.set(false);
    }

    public void shiftLowGear() {
        setting = Setting.LowGear;
        solenoid.set(true);
    }

    public void stopBrakeMode() {
        setting = Setting.Coast;

        leftMotor.setNeutralMode(NeutralMode.Coast);
        rightMotor.setNeutralMode(NeutralMode.Coast);
        leftMotorSlave.setNeutralMode(NeutralMode.Coast);
        rightMotorSlave.setNeutralMode(NeutralMode.Coast);
    }

    public void enableBrakeMode() {
        setting = Setting.Brake;
        leftMotor.setNeutralMode(NeutralMode.Brake);
        leftMotorSlave.setNeutralMode(NeutralMode.Brake);
        rightMotor.setNeutralMode(NeutralMode.Brake);
        rightMotorSlave.setNeutralMode(NeutralMode.Brake);
    }

    public void checkDirection(boolean forwards, VictorSPX victorSPX, boolean defaultDirection) {
        if (forwards) {
            victorSPX.setInverted(!defaultDirection);
        } else {
            victorSPX.setInverted(defaultDirection);
        }
    }

    // ============================================================================================================================

//	MotionProfiling

    public void stopMotionProfiling() {
        leftMotorHelper.stopTalon();
        rightMotorHelper.stopTalon();
        defaultTalonDirection();
        reachedMinPoints = false;
    }

    public void processMotionProfiling() {
        mode = DriveMode.MotionProfile;
        System.out.println("Right Out: " + rightMotor.getMotorOutputPercent() + "Left Out: " + leftMotor.getMotorOutputPercent());
        if (rightMotorHelper.isBufferSufficentlyFull(10/*Defaults.MINIMUM_POINTS_TO_RUN*/) && leftMotorHelper.isBufferSufficentlyFull(10/*Defaults.MINIMUM_POINTS_TO_RUN*/)) {
            reachedMinPoints = true;
        }
        if (reachedMinPoints) {
            leftMotorHelper.processPoints();
            rightMotorHelper.processPoints();
        }
    }

    public boolean isMotionProfilingFinished() {
        return leftMotorHelper.isFinished() && rightMotorHelper.isFinished();
    }

    public void autonDriveForward(List<TrajectoryPoint> trajPointList) {
        resetMotionProfiling(trajPointList, true, false);
    }

    public void autonDriveBackward(List<TrajectoryPoint> trajPointList) {
        resetMotionProfiling(trajPointList, false, true);
    }

    public void autonPointTurn(List<TrajectoryPoint> trajPointList, double angle) {
        if (angle > 0)
            resetMotionProfiling(trajPointList, true, true);
        else
            resetMotionProfiling(trajPointList, false, false);
    }

    public void autonStraightDriveIntoTurn(double linearDistanceInInches, double angleTurn) {
        List<TrajectoryPoint> outsideTpList = MotionProfileGenerator
                .generatePoints(straightIntoTurnConfig(linearDistanceInInches, angleTurn).get(0));
        List<TrajectoryPoint> insideTpList = MotionProfileGenerator
                .generatePoints(straightIntoTurnConfig(linearDistanceInInches, angleTurn).get(1));

        if (angleTurn < 0) {
            leftMotorHelper.resetTalon();
            rightMotorHelper.resetTalon();
            leftMotorHelper.pushPointsDrive(outsideTpList, true);
            rightMotorHelper.pushPointsDrive(insideTpList, false);
        } else {
            leftMotorHelper.resetTalon();
            rightMotorHelper.resetTalon();
            leftMotorHelper.pushPointsDrive(insideTpList, true);
            rightMotorHelper.pushPointsDrive(outsideTpList, false);
        }
    }

    public void autonArcTurn(double angleTurn, double turnRadius, boolean isRightTurn) {
        List<TrajectoryPoint> outsideTpList = MotionProfileGenerator
                .generatePoints(arcTurnConfig(angleTurn, turnRadius, isRightTurn).get(0));
        List<TrajectoryPoint> insideTpList = MotionProfileGenerator
                .generatePoints(arcTurnConfig(angleTurn, turnRadius, isRightTurn).get(1));
        if (isRightTurn) {
            leftMotorHelper.resetTalon();
            rightMotorHelper.resetTalon();
            leftMotorHelper.pushPointsDrive(outsideTpList, true);
            rightMotorHelper.pushPointsDrive(insideTpList, false);
        } else {
            leftMotorHelper.resetTalon();
            rightMotorHelper.resetTalon();
            leftMotorHelper.pushPointsDrive(insideTpList, true);
            rightMotorHelper.pushPointsDrive(outsideTpList, false);
        }
    }

    public void resetMotionProfiling(List<TrajectoryPoint> trajPointList, boolean leftForwards, boolean rightForwards) {
        defaultTalonDirection();
        zeroEncoders();
        leftMotorHelper.reset();
        rightMotorHelper.reset();
        TrajectoryPointPusher trajPointPusher = new TrajectoryPointPusher(leftMotorHelper,
                trajPointList);
        new Thread(trajPointPusher).start();
        trajPointPusher = new TrajectoryPointPusher(rightMotorHelper,
                trajPointList);
        new Thread(trajPointPusher).start();
        leftMotorHelper.checkDirection(leftForwards);
        checkDirection(leftForwards, leftMotorSlave, Defaults.LEFT_SLAVE_MOTOR_DEFAULT_DIRECTION);
        rightMotorHelper.checkDirection(rightForwards);
        checkDirection(rightForwards, rightMotorSlave, Defaults.RIGHT_SLAVE_MOTOR_DEFAULT_DIRECTION);
        leftMotorHelper.setF(Defaults.DRIVETRAIN_FGAIN);
        rightMotorHelper.setF(Defaults.DRIVETRAIN_FGAIN);
    }

    public void resetMotionProfilingAndGeneratePoints(MotionProfileConfiguration config, boolean leftForwards,
                                                      boolean rightForwards) {
        List<TrajectoryPoint> trajPointList = MotionProfileGenerator.generatePoints(config);
        leftMotorHelper.reset();
        rightMotorHelper.reset();
        leftMotorHelper.pushPointsDrive(trajPointList, leftForwards);
        rightMotorHelper.pushPointsDrive(trajPointList, rightForwards);
        leftMotorHelper.setF(Defaults.DRIVETRAIN_FGAIN);
        rightMotorHelper.setF(Defaults.DRIVETRAIN_FGAIN);
    }

    public List<TrajectoryPoint> autonPointTurnTpList(double angle) {
        return MotionProfileGenerator.generatePoints(pointTurnConfig(angle));
    }

    public List<TrajectoryPoint> autonDriveTpList(double linearDistInInches) {
        return MotionProfileGenerator.generatePoints(driveStraigtConfig(linearDistInInches));
    }

    private double practiceBotErrorCorrection(double linearDistance) {
        return ((-5.1007 * Math.pow(10, -6)) * Math.pow(linearDistance, 3)) + (.00229 * Math.pow(linearDistance, 2)) + (.18177 * linearDistance) + 19.02665;
    }

    private double mainBotErrorCorrection(double linearDistance) {
        return ((-6.729 * Math.pow(10, -6)) * Math.pow(linearDistance, 3)) + (.00207 * Math.pow(linearDistance, 2)) + (.67076 * linearDistance) + 5.27641;
    }

    public MotionProfileConfiguration driveStraigtConfig(double linearDistInInches) {
        MotionProfileConfiguration configuration = new MotionProfileConfiguration();
        double errorCorrection = practiceBotErrorCorrection(linearDistInInches);
        double rotationDist = (errorCorrection) / (Drivetrain.WHEEL_CIRCUMFERENCE) * (1.06);
        double encoderRevolutions = rotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        configuration.setEndDistance(encoderRevolutions);
        configuration.setIntervalVal(10);
        configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
        configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR);
        configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
        configuration.setVelocityOnly(false);
        return configuration;
    }

    public MotionProfileConfiguration pointTurnConfig(double angleTurn) {
        MotionProfileConfiguration configuration = new MotionProfileConfiguration();
        double linearDist = (Math.abs(angleTurn) / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI) /* 1.5*/;
        double rotationDist = (linearDist) / (Drivetrain.WHEEL_CIRCUMFERENCE);
        double encoderTics = rotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        configuration.setEndDistance(encoderTics);
        configuration.setIntervalVal(10);
        configuration.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
        configuration.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR);
        configuration.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
        configuration.setVelocityOnly(false);
        return configuration;
    }

    private void pidDriveSetpoint(double leftSetPoint, double rightSetPoint) {
        leftMotor.set(ControlMode.Position, -leftSetPoint);
        rightMotor.set(ControlMode.Position, rightSetPoint);
    }


    private double currentLeftLinearGoal = -10000;
    private double currentRightLinearGoal = -10000;

    public void driveThenPivotPID(double linearDistance, double angle) {
        if (currentLeftLinearGoal == -10000 && currentRightLinearGoal == -10000) {
            zeroEncoders();
        } else {
            System.out.println("FAILED TO ZERO");
        }
        double outsideLinearDistance = (Math.abs(angle) / 360) * 1.963 * (Drivetrain.ROBOT_WIDTH * Math.PI) + linearDistance;
        double outsideRotations = outsideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
        double insideRotations = linearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
        double outsideEncoderTics = outsideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        double insideEncoderTics = insideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        if (angle > 0) {
            currentRightLinearGoal = outsideLinearDistance;
            currentLeftLinearGoal = linearDistance;
            pidDriveSetpoint(insideEncoderTics, outsideEncoderTics);
        } else {
            currentLeftLinearGoal = outsideLinearDistance;
            currentRightLinearGoal = linearDistance;
            pidDriveSetpoint(outsideEncoderTics, insideEncoderTics);
        }

    }

    public void driveStraightPID(double linearDistance) {
        if (currentLeftLinearGoal == -10000) {
            zeroEncoders();
        }
        currentLeftLinearGoal = linearDistance;
        currentRightLinearGoal = linearDistance;
        double leftRotationDist = ((linearDistance) / (Drivetrain.WHEEL_CIRCUMFERENCE));
        double rightRotationDist = ((linearDistance) / (Drivetrain.WHEEL_CIRCUMFERENCE));
        double leftEncoderTics = leftRotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        double rightEncoderTics = rightRotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        if (linearDistance > 0)
            capVoltage();
        else
            lowCapVoltage();
        pidDriveSetpoint(leftEncoderTics, rightEncoderTics);
    }

    private int linearDistanceToTicks(double distance) {
        return (int) (((distance) / (Drivetrain.WHEEL_CIRCUMFERENCE)) * Drivetrain.ENCODER_EDGES_PER_REVOLUTION);
    }

    public void pointTurnPIDDrive(double angle) {
        if (currentLeftLinearGoal == -10000) {
            zeroEncoders();
        }
        double linearDistance = (Math.abs(angle) / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI) * 1.363;
        currentLeftLinearGoal = linearDistance;
        currentRightLinearGoal = linearDistance;
        double leftRotationDist = (linearDistance) / (Drivetrain.WHEEL_CIRCUMFERENCE);
        double rightRotationDist = (linearDistance) / (Drivetrain.WHEEL_CIRCUMFERENCE);
        double leftEncoderTics = leftRotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        double rightEncoderTics = rightRotationDist * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        if (angle > 0)
            pidDriveSetpoint(leftEncoderTics, -rightEncoderTics);
        else
            pidDriveSetpoint(-leftEncoderTics, rightEncoderTics);
    }

    public boolean hasPIDFinished() {
        boolean hasFinished = (-leftMotor.getSelectedSensorPosition(0) > linearDistanceToTicks(currentLeftLinearGoal) - 200 && -leftMotor.getSelectedSensorPosition(0) < linearDistanceToTicks(currentLeftLinearGoal) + 200)
                && rightMotor.getSelectedSensorPosition(0) > linearDistanceToTicks(currentRightLinearGoal) - 200 && rightMotor.getSelectedSensorPosition(0) < linearDistanceToTicks(currentRightLinearGoal) + 200;
        System.out.println("has finished: " + hasFinished + " \t linearDistanceToTics of Right Goal: " + linearDistanceToTicks(currentRightLinearGoal) + " \t  right motor position: " + rightMotor.getSelectedSensorPosition(0) + " \t linearDistanceToTics of left Goal: " + linearDistanceToTicks(currentLeftLinearGoal) + " \t  left motor position: " + leftMotor.getSelectedSensorPosition(0));
        return hasFinished;
    }

    public boolean hasPointTurnPIDFinished(double angle) {
        boolean hasFinished;
        if (angle > 0) {
            hasFinished = (-leftMotor.getSelectedSensorPosition(0) > linearDistanceToTicks(currentLeftLinearGoal) - 200 && -leftMotor.getSelectedSensorPosition(0) < linearDistanceToTicks(currentLeftLinearGoal) + 200)
                    && -rightMotor.getSelectedSensorPosition(0) > linearDistanceToTicks(currentLeftLinearGoal) - 200 && -rightMotor.getSelectedSensorPosition(0) < linearDistanceToTicks(currentLeftLinearGoal) + 200;
        } else {
            hasFinished = (leftMotor.getSelectedSensorPosition(0) > linearDistanceToTicks(currentLeftLinearGoal) - 200 && leftMotor.getSelectedSensorPosition(0) < linearDistanceToTicks(currentLeftLinearGoal) + 200)
                    && rightMotor.getSelectedSensorPosition(0) > linearDistanceToTicks(currentLeftLinearGoal) - 200 && rightMotor.getSelectedSensorPosition(0) < linearDistanceToTicks(currentLeftLinearGoal) + 200;
        }
        return hasFinished;
    }


    public void stopPIDMovement() {
        System.out.println("SETTING GOALS TO 0");
        currentLeftLinearGoal = -10000;
        currentRightLinearGoal = -10000;
    }

    /**
     * List of profiles, first for outside then for inside.
     */
    public ArrayList<MotionProfileConfiguration> arcTurnConfig(double angleTurn, double turnRadius,
                                                               boolean isRightTurn) {
        MotionProfileConfiguration configuration1 = new MotionProfileConfiguration();
        MotionProfileConfiguration configuration2 = new MotionProfileConfiguration();

        double outsideLinearDistance = 2 * Math.PI * (turnRadius + Drivetrain.ROBOT_WIDTH) * (angleTurn / 360);
        double insideLinearDistance = 2 * Math.PI * turnRadius * (angleTurn / 360);
        double outsideRotations = outsideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
        double insideRotations = insideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
        double time = outsideRotations / Drivetrain.AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR;
        double interiorVelocity = insideRotations / time;
        double outsiddeEncoderTics = outsideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        double insideEncoderTics = insideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;

        configuration1.setEndDistance(outsiddeEncoderTics);
        configuration1.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR);
        configuration2.setEndDistance(insideEncoderTics);
        configuration2.setMaxVel(interiorVelocity);

        configuration1.setForwards(true);
        configuration1.setIntervalVal(10);
        configuration1.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
        configuration1.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
        configuration1.setVelocityOnly(false);

        configuration2.setForwards(true);
        configuration2.setIntervalVal(10);
        configuration2.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
        configuration2.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
        configuration2.setVelocityOnly(false);

        ArrayList<MotionProfileConfiguration> configList = new ArrayList<MotionProfileConfiguration>();
        configList.add(configuration1);
        configList.add(configuration2);
        return configList;
    }

    /**
     * List of profiles, first for outside then for inside.
     */
    public ArrayList<MotionProfileConfiguration> straightIntoTurnConfig(double linearDistanceInInches,
                                                                        double angleTurn) {
        MotionProfileConfiguration configuration1 = new MotionProfileConfiguration();
        MotionProfileConfiguration configuration2 = new MotionProfileConfiguration();
        double outsideLinearDistance = (angleTurn / 360) * (Drivetrain.ROBOT_WIDTH * Math.PI) + linearDistanceInInches;
        double outsideRotations = outsideLinearDistance / Drivetrain.WHEEL_CIRCUMFERENCE;
        double insideRotations = linearDistanceInInches / Drivetrain.WHEEL_CIRCUMFERENCE;
        double outsiddeEncoderTics = outsideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        double insideEncoderTics = insideRotations * Drivetrain.ENCODER_EDGES_PER_REVOLUTION;
        configuration1.setEndDistance(outsiddeEncoderTics);
        configuration2.setEndDistance(insideEncoderTics);
        configuration1.setIntervalVal(10);
        configuration1.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
        configuration1.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR);
        configuration1.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
        configuration1.setVelocityOnly(false);
        configuration2.setIntervalVal(10);
        configuration2.setInterval(TrajectoryDuration.Trajectory_Duration_10ms);
        configuration2.setMaxVel(Drivetrain.AUTONOMOUS_MAX_VELOCITY_HIGH_GEAR);
        configuration2.setMaxAcc(Drivetrain.AUTONOMOUS_MAX_ACCELERATION);
        configuration2.setVelocityOnly(false);
        ArrayList<MotionProfileConfiguration> configList = new ArrayList<>();
        configList.add(configuration1);
        configList.add(configuration2);
        return configList;
    }


    // ============================================================================================================================

    //	Loggers
    public void printMotorVoltage() {
        logger.debug("Left Main [{}] Left Slave [{}] Right Main [{}] Right Slave [{}]",
                leftMotor.getMotorOutputVoltage(), leftMotorSlave.getMotorOutputVoltage(),
                rightMotor.getMotorOutputVoltage(), rightMotorSlave.getMotorOutputVoltage());
    }

//	PID Camera Control

    public void setUpPID() {
        CustomPIDService pid = new CustomPIDService(cameraP, cameraI, cameraD);
        pid.reset();
        pid.setMaxIOutput(.000000000000001);
        pid.setDirection(false);
        pid.setSetpoint(0);
        pid.setOutputLimits(0, 1);
    }

    public void cameraAssistedAlign(double alignValue) {
        double output;

        if (Math.abs(alignValue) <= .07)
            output = alignValue;
        else if (Math.abs(alignValue) <= .2)
            output = alignValue;
        else
            output = .5 * alignValue;

        leftMotor.set(ControlMode.PercentOutput, output);
        rightMotor.set(ControlMode.PercentOutput, output);
    }

    public double adjustedTurn(double turn) {
        if (turn > 0) {
            return Math.min((turn * 160.) / 90, 1);
        } else {
            return Math.max((turn * 160.) / 90, -1);
        }
    }
}
