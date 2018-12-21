package org.usfirst.frc.team2073.robot.ctx;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;
import com.team2073.common.speedcontrollers.EagleSPX;
import com.team2073.common.speedcontrollers.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.conf.AppConstants.RobotPorts;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.PowerTakeOffSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeBottomRollerSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class RobotMapModule extends AbstractModule {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void configure() {

        // Drivetrain
        bindNamed(EagleSRX.class, "leftMotor").toInstance(new EagleSRX(RobotPorts.DRIVE_LEFT_MOTOR, "leftMotor", .5));
        bindNamed(EagleSPX.class, "leftMotorSlave").toInstance(new EagleSPX(RobotPorts.DRIVE_LEFT_MOTOR_SLAVE, "leftMotorSlave", .5));
        bindNamed(EagleSRX.class, "rightMotor").toInstance(new EagleSRX(RobotPorts.DRIVE_RIGHT_MOTOR, "rightMotor", .5));
        bindNamed(EagleSPX.class, "rightMotorSlave").toInstance(new EagleSPX(RobotPorts.DRIVE_RIGHT_MOTOR_SLAVE, "rightMotorSlave", .5));
        bindNamed(Solenoid.class, "driveSolenoid").toInstance(new Solenoid(RobotPorts.DRIVE_SOLENOID));
        bind(PigeonIMU.class).toInstance(new PigeonIMU(RobotPorts.PIGEON));

        // Intake
        bindNamed(EagleSRX.class, "leftPivotMotor").toInstance(new EagleSRX(RobotPorts.INTAKE_PIVOT_LEFT, "leftPivotMotor", .5));
        bindNamed(EagleSRX.class, "rightPivotMotor").toInstance(new EagleSRX(RobotPorts.INTAKE_PIVOT_RIGHT, "rightPivotMotor", .5));
        bindNamed(Victor.class, "intakeBottomMotor").toInstance(new Victor(RobotPorts.INTAKE_BOTTOM));
        bindNamed(Victor.class, "intakeLeftSideMotor").toInstance(new Victor(RobotPorts.INTAKE_SIDE_LEFT));
        bindNamed(Victor.class, "intakeRightSideMotor").toInstance(new Victor(RobotPorts.INTAKE_SIDE_RIGHT));
        bindNamed(DigitalInput.class, "leftPivotLimit").toInstance(new DigitalInput(RobotPorts.LEFT_PIVOT_LIMIT));
        bindNamed(DigitalInput.class, "rightPivotLimit").toInstance(new DigitalInput(RobotPorts.RIGHT_PIVOT_LIMIT));

        // Shooter
        bindNamed(EagleSRX.class, "shooterPivotMotor").toInstance(new EagleSRX(RobotPorts.SHOOTER_PIVOT, "shooterPivotMotor", .5));
        bindNamed(EagleSPX.class, "rightShooterMotor").toInstance(new EagleSPX(RobotPorts.SHOOTER_RIGHT, "rightShooterMotor", .5));
        bindNamed(EagleSPX.class, "leftShooterMotor").toInstance(new EagleSPX(RobotPorts.SHOOTER_LEFT, "leftShooterMotor", .5));
        bindNamed(DigitalInput.class, "shooterPivotLimit").toInstance(new DigitalInput(RobotPorts.SHOOTER_PIVOT_LIMIT));
        bindNamed(DigitalInput.class, "shooterBanner").toInstance(new DigitalInput(RobotPorts.SHOOTER_HOLDING_SENSOR));

        // Elevator
        bindNamed(EagleSRX.class, "elevatorMaster").toInstance(new EagleSRX(RobotPorts.ELEVATOR_MASTER, "elevatorMaster", .5));
        bindNamed(EagleSPX.class, "elevatorSlave").toInstance(new EagleSPX(RobotPorts.ELEVATOR_SLAVE, "elevatorSlave", .5));
        bindNamed(Solenoid.class, "elevatorBrake").toInstance(new Solenoid(RobotPorts.ELEVATOR_BRAKE_SOLENOID));
        bindNamed(DigitalInput.class, "elevatorMax").toInstance(new DigitalInput(RobotPorts.ELEVATOR_MAX));
        bindNamed(DigitalInput.class, "elevatorMin").toInstance(new DigitalInput(RobotPorts.ELEVATOR_MIN));

//		PowerTakeOff 
        bindNamed(DigitalInput.class, "talonTachSensor").toInstance(new DigitalInput(RobotPorts.TALONTACH_PTO));
        bindNamed(Solenoid.class, "ptoSolenoid").toInstance(new Solenoid(RobotPorts.PTO_SOLENOID));

        // Subsystems
        bind(DrivetrainSubsystem.class).asEagerSingleton();
        bind(IntakePivotSubsystem.class).asEagerSingleton();
        bind(IntakeBottomRollerSubsystem.class).asEagerSingleton();
        bind(IntakeSideRollerSubsystem.class).asEagerSingleton();
        bind(ShooterSubsystem.class).asEagerSingleton();
        bind(ShooterPivotSubsystem.class).asEagerSingleton();
        bind(ElevatorSubsystem.class).asEagerSingleton();
        bind(PowerTakeOffSubsystem.class).asEagerSingleton();
    }

    private <T> LinkedBindingBuilder<T> bindNamed(Class<T> clazz, String name) {
        return bind(clazz).annotatedWith(Names.named(name));
    }

}