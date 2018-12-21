package org.usfirst.frc.team2073.robot.commands.auto.left;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.commands.shooter.LowVoltageShootCommand;

import javax.annotation.PostConstruct;

public class LeftLeftSwitchLeftScale extends AbstractLoggingCommandGroup {
    @Inject
    private Provider<LowVoltageShootCommand> shootLVProvider;
    @Inject
    private CommandFactory commandFactory;

    @PostConstruct
    public void init() {
        setInterruptible(false);
        addSequential(commandFactory.createMoveForwardMpCommand(115));
        addSequential(commandFactory.createPointTurnMpCommand(95));
        addSequential(commandFactory.createMoveForwardMpCommand(30));
        addSequential(shootLVProvider.get(), .75);
    }

    @Override
    protected void executeDelegate() {
        System.out.printf("Executing %s%n", getClass().getName());
        super.executeDelegate();
    }
}
