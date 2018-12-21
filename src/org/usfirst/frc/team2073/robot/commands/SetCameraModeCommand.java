package org.usfirst.frc.team2073.robot.commands;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingInstantCommand;
import edu.wpi.first.wpilibj.SerialPort;
import org.usfirst.frc.team2073.robot.ctx.SerialPortProvider;
import org.usfirst.frc.team2073.robot.util.CheckedProviderUtils;

public class SetCameraModeCommand extends AbstractLoggingInstantCommand {
    private final SerialPort serialPort;
    private final int cameraMode;

    @Inject
    public SetCameraModeCommand(SerialPortProvider serialPortProvider, @Assisted int cameraMode) {
        this.serialPort = CheckedProviderUtils.getOrNull(serialPortProvider);
        if (serialPort == null)
            logger.error("Camera serial port cannot be null. Camera mode will not be set!");
        this.cameraMode = cameraMode;
    }

    @Override
    protected void initializeDelegate() {
        if (serialPort != null)
            serialPort.writeString(cameraMode + "\n");
    }
}
