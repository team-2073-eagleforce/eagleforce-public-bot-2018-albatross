package org.usfirst.frc.team2073.robot.ctx;

import com.google.inject.Inject;
import com.team2073.common.util.CommandUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.CommandInjector;
import org.usfirst.frc.team2073.robot.commands.TogglePTOCommand;
import org.usfirst.frc.team2073.robot.commands.drive.ManualPointTurnCommand;
import org.usfirst.frc.team2073.robot.commands.drive.ShiftHighGearCommand;
import org.usfirst.frc.team2073.robot.commands.elevator.ElevatorDisableBikeBrakeCommand;
import org.usfirst.frc.team2073.robot.commands.elevator.ZeroElevatorDownCommand;
import org.usfirst.frc.team2073.robot.commands.elevator.ZeroElevatorUpCommand;
import org.usfirst.frc.team2073.robot.commands.intake.*;
import org.usfirst.frc.team2073.robot.commands.shooter.*;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Camera;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Controllers.DriveWheel;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Controllers.PowerStick;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Controllers.Xbox;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Elevator;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Shooter;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team2073.robot.triggers.*;
import org.usfirst.frc.team2073.robot.util.inject.InjectNamed;

import javax.annotation.PostConstruct;

public class OperatorInterface {
    @InjectNamed
    private DigitalInput leftPivotLimit;
    @InjectNamed
    private DigitalInput rightPivotLimit;
    @InjectNamed
    private DigitalInput elevatorMax;
    @InjectNamed
    private DigitalInput elevatorMin;
    @InjectNamed
    private DigitalInput shooterPivotLimit;
    @InjectNamed
    private DigitalInput shooterBanner;

    @InjectNamed
    private Joystick controller;
    @InjectNamed
    private Joystick joystick;
    @InjectNamed
    private Joystick wheel;

    @Inject
    private ZeroLeftIntakeCommand zeroLeftIntake;
    @Inject
    private ZeroRightIntakeCommand zeroRightIntake;
    @Inject
    private ZeroShooterPivotCommand zeroShooterPivotCommand;
    @Inject
    private IntakeAllCommandGroup allIntakeSpin;
    @Inject
    private ManualPointTurnCommand pointTurn;
    @Inject
    private ZeroElevatorDownCommand zeroElevator;
    @Inject
    private ZeroElevatorUpCommand maxElevator;
    @Inject
    private ShiftHighGearCommand shiftHighGear;
    @Inject
    private ElevatorDisableBikeBrakeCommand releaseBreak;
    @Inject
    private IntakeHoldPresetCommand intakeHoldPresetCommand;
    @Inject
    private IntakeZeroPresetCommand zeroIntakePresetCommand;
    @Inject
    private IntakePivotOutPresetCommand outIntakePresetCommand;
    @Inject
    private ShooterHoldCubeCommand shooterHoldCubeCommand;
    @Inject
    private OuttakeShooterCommand shootCommand;
    @Inject
    private LowVoltageShootCommand lvShoot;
    @Inject
    private TogglePTOCommand togglePTO;
    @Inject
    private RumbleControllersCommand rumbleControllersCommand;
    @Inject
    private CommandFactory commandFactory;
    @Inject
    private CommandInjector commandInjector;
    @Inject
    private ElevatorSubsystem elevatorSys;
    @Inject
    private IntakeStowCommand intakeStow;
    @Inject
    private IntakeSameTimeOutCommand intakeSameTimeOut;
    @Inject
    private MaxShootCommand maxShoot;

    @PostConstruct
    public void init() {
        Command elevatorToSwitchCommand = commandFactory.createElevatorSetPointCommand(Elevator.SWITCH_HEIGHT);
        Command elevatorToZeroCommand = commandFactory.createElevatorSetPointCommand(Elevator.BOTTOM_HEIGHT);
        Command elevatorToPivotCommand = commandFactory.createElevatorSetPointCommand(Elevator.PIVOT_HEIGHT);
        Command elevatorToMaxCommand = commandFactory.createElevatorSetPointCommand(Elevator.MAX_TRAVEL);
        Command elevatorToLowScaleCommand = commandFactory.createElevatorSetPointCommand(Elevator.LOW_SCALE);
        Command pivotShooterToBackCommand = commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.BACK_SHOOT);
        Command pivotShooterTo90 = commandFactory.createShooterPivotSetpointCommand(90);

        Command setCube = commandFactory.createSetCameraModeCommand(Camera.Mode.CUBE);
        Command setArUco = commandFactory.createSetCameraModeCommand(Camera.Mode.ARUCO);

        Trigger leftIntakeLimit = new LimitSwitch(leftPivotLimit);
        Trigger rightIntakeLimit = new LimitSwitch(rightPivotLimit);
        Trigger elevatorMinTrigger = new HallEffect(elevatorMin);
        Trigger elevatorMaxTrigger = new HallEffect(elevatorMax);
        Trigger shooterPivotLimitTrigger = new HallEffect(shooterPivotLimit);
        Trigger shooterBannerTrigger = new BannerSensor(shooterBanner);
        Trigger leftDPad = new DPadTrigger(controller, 270);
        Trigger upDPad = new DPadTrigger(controller, 0);
        Trigger rightDPad = new DPadTrigger(controller, 90);
        Trigger downDPad = new DPadTrigger(controller, 180);
        Trigger leftTrigger = new TriggerTrigger(controller, Xbox.Axes.LEFT_TRIGGER);
        Trigger rightTrigger = new TriggerTrigger(controller, Xbox.Axes.RIGHT_TRIGGER);
        Trigger elevatorAboveBottom = new ElevatorHeightTrigger(Elevator.SWITCH_HEIGHT - 5, elevatorSys);
        Trigger notAutonTrigger = new NotAutonTrigger();
        Trigger shooterAndNotAutonTrigger = new MultiTrigger(shooterBannerTrigger, notAutonTrigger);

        JoystickButton a = new JoystickButton(controller, Xbox.ButtonPorts.A);
        JoystickButton b = new JoystickButton(controller, Xbox.ButtonPorts.B);
        JoystickButton y = new JoystickButton(controller, Xbox.ButtonPorts.Y);
        JoystickButton x = new JoystickButton(controller, Xbox.ButtonPorts.X);
        JoystickButton start = new JoystickButton(controller, Xbox.ButtonPorts.START);
        JoystickButton back = new JoystickButton(controller, Xbox.ButtonPorts.BACK);
        JoystickButton rb = new JoystickButton(controller, Xbox.ButtonPorts.R1);
        JoystickButton lb = new JoystickButton(controller, Xbox.ButtonPorts.L1);

        JoystickButton leftPaddle = new JoystickButton(wheel, DriveWheel.ButtonPorts.LPADDLE);
        JoystickButton rightPaddle = new JoystickButton(wheel, DriveWheel.ButtonPorts.RPADDLE);

        JoystickButton power1 = new JoystickButton(joystick, PowerStick.ButtonPorts.TRIGGER);
        JoystickButton power4 = new JoystickButton(joystick, PowerStick.ButtonPorts.LEFT);
        JoystickButton power5 = new JoystickButton(joystick, PowerStick.ButtonPorts.RIGHT);
        JoystickButton power3 = new JoystickButton(joystick, PowerStick.ButtonPorts.CENTER);
        JoystickButton power2 = new JoystickButton(joystick, PowerStick.ButtonPorts.BOTTOM);
        Trigger bothPaddles = new MultiTrigger(leftPaddle, rightPaddle);

        // ZEROING
        leftIntakeLimit.whileActive(zeroLeftIntake);
        rightIntakeLimit.whileActive(zeroRightIntake);
        elevatorMinTrigger.whileActive(zeroElevator);
        elevatorMaxTrigger.whileActive(maxElevator);
        shooterPivotLimitTrigger.whileActive(zeroShooterPivotCommand);

        // ======================================================================

        // AlwaysRunningInEnabled
        shooterBannerTrigger.whileActive(shooterHoldCubeCommand);
        shooterBannerTrigger.whenActive(rumbleControllersCommand);
        shooterBannerTrigger.whenActive(pivotShooterTo90);
        shooterBannerTrigger.whenActive(CommandUtil.parallelDelayParallel(intakeSameTimeOut, commandInjector.createCommand(IntakeStowCommand.class), 1.25));

        // ======================================================================

        // DRIVE
        power4.whileHeld(shiftHighGear);
        power3.whileHeld(shootCommand);
        power2.whileHeld(lvShoot);
//		power1.whileHeld(cameraAssistedTurn);
        power1.whileHeld(setArUco);
        power5.whileHeld(maxShoot);

        leftPaddle.whileHeld(pointTurn);
        bothPaddles.toggleWhenActive(togglePTO);

        // ======================================================================

        // CONTROLLER
        back.whileHeld(releaseBreak);
        start.whenPressed(zeroIntakePresetCommand);
        leftTrigger.whenActive(outIntakePresetCommand);
        rightTrigger.whenActive(intakeHoldPresetCommand);
        rightTrigger.whileActive(allIntakeSpin);
        a.whileHeld(allIntakeSpin);
        b.whenPressed(elevatorToLowScaleCommand);
        rb.whenPressed(commandFactory.createDriveAndPivotPIDCommand(40));
        y.whenPressed(intakeStow);
        leftDPad.whenActive(elevatorToPivotCommand);
        upDPad.whenActive(elevatorToMaxCommand);
        rightDPad.whenActive(elevatorToSwitchCommand);
        downDPad.whenActive(elevatorToZeroCommand);
        downDPad.whenActive(outIntakePresetCommand);

    }
}