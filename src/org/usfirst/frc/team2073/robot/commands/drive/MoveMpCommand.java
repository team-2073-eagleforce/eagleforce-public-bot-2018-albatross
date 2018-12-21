package org.usfirst.frc.team2073.robot.commands.drive;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Example usage:
 *
 * <pre>
 * &commat;Inject private CommandFactory commandFactory;
 *
 * commandFactory.createMoveMpCommand(Direction.FORWARD, 20);
 * </pre>
 */
public class MoveMpCommand extends AbstractLoggingCommand {
    @Inject
    private DrivetrainSubsystem drivetrain;
    @Inject
    @Assisted
    private Direction direction;
    @Inject
    @Assisted
    private double distance;
    private List<TrajectoryPoint> tpList;

    public enum Direction {
        FORWARD, BACKWARD
    }

    @PostConstruct
    public void init() {
        tpList = drivetrain.autonDriveTpList(distance);
        requires(drivetrain);
    }

    @Override
    protected void initializeDelegate() {
        switch (direction) {
            case FORWARD:
                drivetrain.autonDriveForward(tpList);
                break;
            case BACKWARD:
                drivetrain.autonDriveBackward(tpList);
                break;
        }
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
