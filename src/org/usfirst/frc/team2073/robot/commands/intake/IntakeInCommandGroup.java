package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;

import javax.annotation.PostConstruct;

public class IntakeInCommandGroup extends AbstractLoggingCommandGroup {
    @Inject
    private CommandFactory commandFactory;

    @PostConstruct
    public void init() {
        addParallel(commandFactory.createRightIntakePresetNoHoldCommand(180, false));
        addSequential(new WaitCommand(.3));
        addParallel(commandFactory.createLeftIntakePresetCommand(90, true));
        addSequential(new WaitCommand(.3));
        addParallel(commandFactory.createRightIntakePresetCommand(80, true));
    }
}
