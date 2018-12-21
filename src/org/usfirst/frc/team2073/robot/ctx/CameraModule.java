package org.usfirst.frc.team2073.robot.ctx;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.svc.camera.CameraMessageParser;
import com.team2073.common.svc.camera.CameraMessageReceiver;
import com.team2073.common.svc.camera.CameraMessageReceiverSerialImpl;
import com.team2073.common.svc.camera.CameraMessageService;
import edu.wpi.first.wpilibj.SerialPort;
import org.usfirst.frc.team2073.robot.domain.CameraMessage;
import org.usfirst.frc.team2073.robot.svc.camera.CameraMessageParserArucoCubeImpl;

public class CameraModule extends AbstractModule {
    @Override
    protected void configure() {
        install(ThrowingProviderBinder.forModule(this));
    }

    @CheckedProvides(SerialPortProvider.class)
    @Singleton
    private SerialPort provideSerialPort() {
        return new SerialPort(921600, SerialPort.Port.kUSB);
    }

    @Provides
    private CameraMessageParser<CameraMessage> provideCameraMessageParser(
            SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        return new CameraMessageParserArucoCubeImpl(smartDashboardAwareRegistry);
    }

    @Provides
    private CameraMessageReceiver provideCameraMessageReceiver(
            SerialPortProvider serialPortProvider, SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        return new CameraMessageReceiverSerialImpl(serialPortProvider, smartDashboardAwareRegistry);
    }

    @Provides
    @Singleton
    private CameraMessageService<CameraMessage> provideCameraMessageService(
            CameraMessageParser<CameraMessage> parser, CameraMessageReceiver receiver) {
        return new CameraMessageService<CameraMessage>(parser, receiver, new CameraMessage());
    }
}
