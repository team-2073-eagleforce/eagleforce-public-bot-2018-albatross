package org.usfirst.frc.team2073.robot.commands.auto;

import com.google.inject.Inject;
import com.google.inject.Provider;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.intake.IntakeHoldPresetCommand;
import org.usfirst.frc.team2073.robot.commands.intake.IntakePivotOutPresetCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

import javax.annotation.PostConstruct;

public class AutonIntakeCommandGroupWithDriving extends CommandGroup {
    @Inject
    private ShooterSubsystem shooter;
    @Inject
    private Provider<IntakeHoldPresetCommand> intakeHoldPresetCommand;
    @Inject
    private Provider<IntakePivotOutPresetCommand> intakePivotOut;
    @Inject
    private CommandFactory commandFactory;

    @PostConstruct
    public void init() {
        addParallel(commandFactory.createMoveStraightPIDCommand(5));
        addParallel(intakeHoldPresetCommand.get(), .5);
        addSequential(new WaitCommand(.4));
        addParallel(intakePivotOut.get());
        addSequential(new WaitCommand(.3));
        addParallel(intakeHoldPresetCommand.get(), .5);
        addSequential(commandFactory.createMoveStraightPIDCommand(5));
        addSequential(new WaitCommand(.4));
        addParallel(intakePivotOut.get());
        addSequential(new WaitCommand(.3));
        addParallel(intakeHoldPresetCommand.get(), .5);
        addSequential(commandFactory.createMoveStraightPIDCommand(5));
        addSequential(new WaitCommand(.8));
    }

    @Override
    protected boolean isFinished() {
        return shooter.doesHaveCube() || super.isFinished();
    }
}
