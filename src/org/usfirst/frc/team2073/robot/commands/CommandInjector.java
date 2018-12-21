package org.usfirst.frc.team2073.robot.commands;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.wpi.first.wpilibj.command.Command;

@Singleton
public class CommandInjector {
    @Inject
    private Injector injector;

    public <T extends Command> T createCommand(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
