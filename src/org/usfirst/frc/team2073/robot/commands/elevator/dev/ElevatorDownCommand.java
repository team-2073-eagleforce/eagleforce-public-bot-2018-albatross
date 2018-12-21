package org.usfirst.frc.team2073.robot.commands.elevator.dev;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorDownCommand extends AbstractLoggingCommand {
    private final ElevatorSubsystem elevator;

    @Inject
    public ElevatorDownCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        requires(elevator);
    }

    @Override
    protected void executeDelegate() {
        elevator.getDev().elevatorDown();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
