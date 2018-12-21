package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;

import javax.annotation.PostConstruct;

public class IntakeStowSequenceCommandGroup extends AbstractLoggingCommandGroup {
    @Inject
    private CommandFactory commandFactory;
    @Inject
    private IntakeStowCommand intakeStow;


    public IntakeStowSequenceCommandGroup() {
        setInterruptible(false);
    }

    @PostConstruct
    public void init() {
        addParallel(commandFactory.createLeftIntakePresetCommand(200, false));
        addSequential(commandFactory.createRightIntakePresetCommand(200, false), .6);
        addSequential(intakeStow);
    }
}
