package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

import javax.annotation.PostConstruct;

public class IntakeManualStowCommand extends AbstractLoggingCommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private CommandFactory commandFactotry;

    @Inject
    public IntakeManualStowCommand(IntakePivotSubsystem intake) {
        IntakePivotSubsystem intake1 = intake;
        requires(intake);
    }

    @PostConstruct
    public void init() {
        addParallel(commandFactotry.createLeftIntakePresetCommand(105, false));
        addSequential(new WaitCommand(.25));
        addParallel(commandFactotry.createRightIntakePresetCommand(120, false));
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
