package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class MoveForwardPIDCommand extends AbstractLoggingCommand {
    private final DrivetrainSubsystem drivetrain;
    private final double distance;

    @Inject
    public MoveForwardPIDCommand(DrivetrainSubsystem drivetrain, @Assisted double distance) {
        this.drivetrain = drivetrain;
        this.distance = distance;
        requires(drivetrain);
    }

    @Override
    protected void initializeDelegate() {
    }

    @Override
    protected void executeDelegate() {
        drivetrain.driveStraightPID(distance);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return drivetrain.hasPIDFinished();
    }

    @Override
    protected void endDelegate() {
        drivetrain.stopPIDMovement();
    }
}
