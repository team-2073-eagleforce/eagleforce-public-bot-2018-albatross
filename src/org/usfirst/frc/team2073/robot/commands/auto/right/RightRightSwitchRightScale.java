package org.usfirst.frc.team2073.robot.commands.auto.right;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.shooter.LowVoltageShootCommand;

import javax.annotation.PostConstruct;

public class RightRightSwitchRightScale extends AbstractLoggingCommandGroup {
    @Inject
    private Provider<LowVoltageShootCommand> shootLVProvider;
    @Inject
    private CommandFactory commandFactory;

    @PostConstruct
    public void init() {
        setInterruptible(false);
        addSequential(commandFactory.createMoveForwardMpCommand(115));
        addSequential(commandFactory.createPointTurnMpCommand(-90));
        addSequential(commandFactory.createMoveForwardMpCommand(30));
        addSequential(shootLVProvider.get(), .75);
    }
}
