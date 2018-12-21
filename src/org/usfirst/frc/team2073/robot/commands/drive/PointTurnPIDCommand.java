package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class PointTurnPIDCommand extends AbstractLoggingCommand {
    private final DrivetrainSubsystem drivetrain;
    private final double angle;

    @Inject
    public PointTurnPIDCommand(DrivetrainSubsystem drivetrain, @Assisted double angle) {
        this.drivetrain = drivetrain;
        this.angle = angle;
        requires(drivetrain);
    }

    @Override
    protected void initializeDelegate() {
    }

    @Override
    protected void executeDelegate() {
        drivetrain.pointTurnPIDDrive(angle);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return drivetrain.hasPointTurnPIDFinished(angle);
    }

    @Override
    protected void endDelegate() {
        drivetrain.stopPIDMovement();
    }
}
