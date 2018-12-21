package org.usfirst.frc.team2073.robot.commands.drive;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

import java.util.List;

public class MoveForwardMpCommand extends AbstractLoggingCommand {
    private final DrivetrainSubsystem drivetrain;
    private final List<TrajectoryPoint> tpList;

    @Inject
    public MoveForwardMpCommand(DrivetrainSubsystem drivetrain, @Assisted double distance) {
        this.drivetrain = drivetrain;
        this.tpList = drivetrain.autonDriveTpList(distance);
        requires(drivetrain);
    }

    @Override
    protected void initializeDelegate() {
        drivetrain.autonDriveForward(tpList);
    }

    @Override
    protected void executeDelegate() {
        drivetrain.processMotionProfiling();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return drivetrain.isMotionProfilingFinished();
    }

    @Override
    protected void endDelegate() {
        drivetrain.stopMotionProfiling();
    }
}
