package org.usfirst.frc.team2073.robot.commands.elevator;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ZeroElevatorUpCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ElevatorSubsystem elevator;

    @Inject
    public ZeroElevatorUpCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        setRunWhenDisabled(true);
    }

    @Override
    protected void executeDelegate() {
        elevator.maxEncoder();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return true;
    }

}
