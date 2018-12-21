package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import com.team2073.common.svc.camera.CameraMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.domain.CameraMessage;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class CameraFollowCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean startedExecute = false;

    private final DrivetrainSubsystem drivetrain;
    private final CameraMessageService<CameraMessage> arucoCameraSvc;

    @Inject
    public CameraFollowCommand(DrivetrainSubsystem drivetrain, CameraMessageService<CameraMessage> arucoCameraSvc) {
        this.drivetrain = drivetrain;
        this.arucoCameraSvc = arucoCameraSvc;
        requires(drivetrain);
    }

    @Override
    protected void initializeDelegate() {
        logger.trace("CameraFollowCommand is initializing");
        drivetrain.setUpPID();
    }

    @Override
    protected void executeDelegate() {
        if (!startedExecute) {
            logger.trace("CameraFollowCommand started executing");
            startedExecute = true;
        }
        drivetrain.cameraAssistedAlign(arucoCameraSvc.currentMessage().getArAlign());
    }

    @Override
    protected boolean isFinishedDelegate() {
        logger.trace("CameraFollowCommand is finished");
        return false;
    }

    @Override
    protected void interruptedDelegate() {
        logger.trace("CameraFollowCommmand was interrupted");
        super.interrupted();
    }
}
