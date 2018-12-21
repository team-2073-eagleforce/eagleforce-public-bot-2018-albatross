package org.usfirst.frc.team2073.robot.commands.elevator.mp;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorToMinCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ElevatorSubsystem elevator;

    @Inject
    public ElevatorToMinCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        requires(elevator);
    }

    @Override
    protected void initializeDelegate() {
        elevator.releaseBrake();
        elevator.goToReceive();
    }

    @Override
    protected void executeDelegate() {
        elevator.processMotionProfiling();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return elevator.isMotionProfilingFinished() || elevator.isAtBottom();
    }

    @Override
    protected void endDelegate() {
        elevator.stopMotionProfiling();
        elevator.brakeElevator();
    }
}
