package org.usfirst.frc.team2073.robot.commands.auto;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;

import javax.annotation.PostConstruct;

public class DriveForwardOnly extends AbstractLoggingCommandGroup {
    @Inject
    private CommandFactory commandFactory;

    @PostConstruct
    public void init() {
        setInterruptible(false);
        addParallel(commandFactory.createShooterPivotSetpointCommand(75));
        addSequential(commandFactory.createMoveStraightPIDCommand(115));
    }

    @Override
    protected void executeDelegate() {
        System.out.printf("Executing %s%n", getClass().getName());
        super.executeDelegate();
    }
}
