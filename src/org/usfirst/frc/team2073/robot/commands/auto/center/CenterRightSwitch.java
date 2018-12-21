package org.usfirst.frc.team2073.robot.commands.auto.center;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.auto.AutonIntakeCommandGroupWithDriving;
import org.usfirst.frc.team2073.robot.commands.intake.IntakeAllCommandGroup;
import org.usfirst.frc.team2073.robot.commands.intake.IntakePivotOutPresetCommand;
import org.usfirst.frc.team2073.robot.commands.intake.IntakeStowCommand;
import org.usfirst.frc.team2073.robot.commands.intake.StopSideIntakeCommand;
import org.usfirst.frc.team2073.robot.commands.shooter.MaxShootCommand;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Shooter;

import javax.annotation.PostConstruct;

public class CenterRightSwitch extends AbstractLoggingCommandGroup {
    @Inject
    private IntakePivotOutPresetCommand pivotIntakesOut;
    @Inject
    private Provider<MaxShootCommand> maxShoot;
    @Inject
    private CommandFactory commandFactory;
    @Inject
    private Provider<IntakeStowCommand> intakeStow;
    @Inject
    private Provider<IntakeAllCommandGroup> intakeAll;
    @Inject
    private Provider<StopSideIntakeCommand> stopIntakeRollers;
    @Inject
    private AutonIntakeCommandGroupWithDriving autonIntakeWithDrive;

    @PostConstruct
    public void init() {
        setInterruptible(false);
        addParallel(commandFactory.createShooterPivotSetpointCommand(40));
        addSequential(commandFactory.createMoveStraightPIDCommand(20), 2);
        addSequential(commandFactory.createPointTurnPIDCommand(45), 2);
        addSequential(commandFactory.createMoveStraightPIDCommand(66), 2);
        addSequential(commandFactory.createPointTurnPIDCommand(-45), 1.5);
        addSequential(commandFactory.createMoveStraightPIDCommand(42), 2);
        addSequential(maxShoot.get(), .5);
        addSequential(commandFactory.createMoveStraightPIDCommand(-23));
        addParallel(pivotIntakesOut);
        addSequential(commandFactory.createPointTurnPIDCommand(-71), 1.5);
        addParallel(commandFactory.createShooterPivotSetpointCommand(Shooter.PivotAngles.INTAKE));
        addParallel(intakeAll.get());
        addSequential(commandFactory.createMoveStraightPIDCommand(22));
        addSequential(autonIntakeWithDrive);
        addParallel(commandFactory.createShooterPivotSetpointCommand(55));
        addSequential(commandFactory.createMoveStraightPIDCommand(-32), 2);
        addParallel(stopIntakeRollers.get());
        addParallel(intakeStow.get());
        addSequential(commandFactory.createPointTurnPIDCommand(72), 1.5);
        addSequential(commandFactory.createMoveStraightPIDCommand(25));
        addSequential(maxShoot.get(), .75);
    }
}
