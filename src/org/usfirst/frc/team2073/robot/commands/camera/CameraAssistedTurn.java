package org.usfirst.frc.team2073.robot.commands.camera;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import com.team2073.common.svc.camera.CameraMessageService;
import org.usfirst.frc.team2073.robot.domain.CameraMessage;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class CameraAssistedTurn extends AbstractLoggingCommand {

    @Inject
    private CameraMessageService<CameraMessage> cameraMessageService;
    private DrivetrainSubsystem drivetrainSubsystem;

    @Inject
    public CameraAssistedTurn(DrivetrainSubsystem drivetrainSubsystem) {
        requires(drivetrainSubsystem);
        this.drivetrainSubsystem = drivetrainSubsystem;
    }

    @Override
    protected void initializeDelegate() {
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

    @Override
    protected void executeDelegate() {
        CameraMessage msg = cameraMessageService.currentMessage();

        drivetrainSubsystem.cameraAssistedAlign(msg.getArAlign());
    }
}
