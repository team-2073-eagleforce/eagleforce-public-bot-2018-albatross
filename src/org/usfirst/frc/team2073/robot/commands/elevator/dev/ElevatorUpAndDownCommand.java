package org.usfirst.frc.team2073.robot.commands.elevator.dev;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import edu.wpi.first.wpilibj.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorUpAndDownCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ElevatorSubsystem elevator;

    @Inject
    public ElevatorUpAndDownCommand(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        requires(elevator);
    }

    @Override
    protected void executeDelegate() {
        elevator.getDev().elevatorUp();
        logger.trace("Going up!");
        Timer.delay(1);
        elevator.getDev().elevatorDown();
        logger.trace("Going down!");
        Timer.delay(1);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
