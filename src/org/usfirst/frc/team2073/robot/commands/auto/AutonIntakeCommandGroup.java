package org.usfirst.frc.team2073.robot.commands.auto;

import com.google.inject.Inject;
import com.google.inject.Provider;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import org.usfirst.frc.team2073.robot.commands.intake.IntakeAllCommandGroup;
import org.usfirst.frc.team2073.robot.commands.intake.IntakeHoldPresetCommand;
import org.usfirst.frc.team2073.robot.commands.intake.IntakePivotOutPresetCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

import javax.annotation.PostConstruct;

public class AutonIntakeCommandGroup extends CommandGroup {
    @Inject
    private ShooterSubsystem shooter;
    @Inject
    private Provider<IntakeHoldPresetCommand> intakeHoldPresetCommand;
    @Inject
    private Provider<IntakeAllCommandGroup> intakeAll;
    @Inject
    private Provider<IntakePivotOutPresetCommand> intakePivotOut;

    @PostConstruct
    public void init() {
        addParallel(intakeHoldPresetCommand.get(), .5);
        addParallel(intakeAll.get());
        addSequential(new WaitCommand(.3));
        addSequential(intakeHoldPresetCommand.get(), .5);
        addSequential(intakePivotOut.get());
        addParallel(intakeAll.get());
        addSequential(new WaitCommand(.3));
        addSequential(intakeHoldPresetCommand.get(), .5);
        addSequential(intakePivotOut.get());
        addParallel(intakeAll.get());
        addSequential(new WaitCommand(.3));
        addSequential(intakeHoldPresetCommand.get(), .5);
        addParallel(intakeHoldPresetCommand.get(), 1);
        addParallel(intakeAll.get());
        addSequential(intakePivotOut.get());
    }

    @Override
    protected boolean isFinished() {
        return shooter.doesHaveCube();
    }
}
