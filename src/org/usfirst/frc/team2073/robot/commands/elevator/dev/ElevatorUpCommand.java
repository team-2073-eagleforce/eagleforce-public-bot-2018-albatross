package org.usfirst.frc.team2073.robot.commands.elevator.dev;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorUpCommand extends AbstractLoggingCommand {
    private final ElevatorSubsystem elevator;

    @Inject
    public ElevatorUpCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        requires(elevator);
    }

    @Override
    protected void executeDelegate() {
        elevator.getDev().elevatorUp();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

    @Override
    protected void endDelegate() {
        elevator.stopMotors();
    }
}
