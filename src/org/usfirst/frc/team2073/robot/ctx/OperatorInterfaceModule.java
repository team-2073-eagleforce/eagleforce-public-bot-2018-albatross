package org.usfirst.frc.team2073.robot.ctx;

import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Controllers.DriveWheel;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Controllers.PowerStick;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Controllers.Xbox;

public class OperatorInterfaceModule extends AbstractModule {
    @Override
    protected void configure() {
        bindNamed(Joystick.class, "controller").toInstance(new Joystick(Xbox.PORT));
        bindNamed(Joystick.class, "joystick").toInstance(new Joystick(PowerStick.PORT));
        bindNamed(Joystick.class, "wheel").toInstance(new Joystick(DriveWheel.PORT));
    }

    private <T> LinkedBindingBuilder<T> bindNamed(Class<T> clazz, String name) {
        return bind(clazz).annotatedWith(Names.named(name));
    }
}
