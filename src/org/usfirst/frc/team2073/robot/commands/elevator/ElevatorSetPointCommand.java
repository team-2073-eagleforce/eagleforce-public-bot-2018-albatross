package org.usfirst.frc.team2073.robot.commands.elevator;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorSetPointCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ElevatorSubsystem elevator;
    private double height;

    @Inject
    public ElevatorSetPointCommand(ElevatorSubsystem elevator, @Assisted double height) {
        this.elevator = elevator;
        this.height = height;
        setInterruptible(false);
        requires(elevator);
    }

    @Override
    protected void initializeDelegate() {
        elevator.elevatorSetPoint(height);
    }

    @Override
    protected void executeDelegate() {
        elevator.moveToSetPoint();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return elevator.hasReachedPosition(height);
    }

    @Override
    protected void endDelegate() {
        elevator.brakeElevator();
    }
}
