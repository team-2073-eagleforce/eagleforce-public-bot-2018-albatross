package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class DriveAndPivotPIDCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DrivetrainSubsystem drivetrain;
    private final double distance;

    @Inject
    public DriveAndPivotPIDCommand(DrivetrainSubsystem drivetrain, @Assisted double driveDistance) {
        this.drivetrain = drivetrain;
        this.distance = driveDistance;
        requires(drivetrain);
    }

    @Override
    protected void initializeDelegate() {
    }

    @Override
    protected void executeDelegate() {
        double angle = 45;
        drivetrain.driveThenPivotPID(distance, angle);
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
