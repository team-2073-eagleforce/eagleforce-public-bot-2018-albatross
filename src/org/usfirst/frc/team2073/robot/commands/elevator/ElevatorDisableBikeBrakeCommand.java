package org.usfirst.frc.team2073.robot.commands.elevator;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorDisableBikeBrakeCommand extends AbstractLoggingCommand {
    private final ElevatorSubsystem elevator;

    @Inject
    public ElevatorDisableBikeBrakeCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        setInterruptible(false);
    }

    @Override
    protected void initializeDelegate() {
        elevator.releaseBrake();
    }

    @Override
    protected void executeDelegate() {
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

    @Override
    protected void endDelegate() {
        elevator.brakeElevator();
    }


}
