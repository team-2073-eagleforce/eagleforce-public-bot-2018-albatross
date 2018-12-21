package org.usfirst.frc.team2073.robot.util;

import com.google.inject.Singleton;
import edu.wpi.first.wpilibj.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Camera;
import org.usfirst.frc.team2073.robot.ctx.SerialPortProvider;

import javax.annotation.PostConstruct;

@Singleton
public class SetCameraModeService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private SerialPort serialPort;

    @PostConstruct
    public void init(SerialPortProvider serialPortProvider) {
        serialPort = CheckedProviderUtils.getOrNull(serialPortProvider);
        if (serialPort == null)
            logger.error("Camera serial port cannot be null. Camera mode will not be set!");
    }

    public void setCameraMode(CameraMode cameraMode) {
        if (serialPort != null)
            serialPort.writeString(cameraMode.getValue() + "\n");
    }

    public enum CameraMode {
        NONE(Camera.Mode.NONE), CUBE(Camera.Mode.CUBE), ARUCO(Camera.Mode.ARUCO);

        private final int value;

        CameraMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return super.toString() + String.format("[value=%s]", value);
        }
    }
}
