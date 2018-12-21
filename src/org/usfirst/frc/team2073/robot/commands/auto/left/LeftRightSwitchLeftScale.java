package org.usfirst.frc.team2073.robot.commands.auto.left;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;

import javax.annotation.PostConstruct;

public class LeftRightSwitchLeftScale extends AbstractLoggingCommandGroup {
    @Inject
    private CommandFactory commandFactory;

    @PostConstruct
    public void init() {
        setInterruptible(false);
        addSequential(commandFactory.createMoveForwardMpCommand(160));
    }
}
