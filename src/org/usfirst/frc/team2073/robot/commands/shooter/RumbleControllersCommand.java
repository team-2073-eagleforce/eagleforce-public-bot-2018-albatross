package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;
import org.usfirst.frc.team2073.robot.util.EagleTimer;
import org.usfirst.frc.team2073.robot.util.inject.InjectNamed;

import javax.annotation.PostConstruct;

public class RumbleControllersCommand extends AbstractLoggingCommand {
    @Inject
    private ShooterSubsystem shooter;
    @InjectNamed
    private Joystick controller;
    @InjectNamed
    private Joystick wheel;
    private EagleTimer timer = new EagleTimer();

    @Override
    protected void initializeDelegate() {
        controller.setRumble(RumbleType.kLeftRumble, .5);
        controller.setRumble(RumbleType.kRightRumble, .5);
        wheel.setRumble(RumbleType.kLeftRumble, .5);
        wheel.setRumble(RumbleType.kRightRumble, .5);
        timer.startTimer();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return timer.hasWaited(1000);
    }

    @Override
    protected void endDelegate() {
        timer.reset();
        controller.setRumble(RumbleType.kLeftRumble, 0);
        controller.setRumble(RumbleType.kRightRumble, 0);
        wheel.setRumble(RumbleType.kLeftRumble, 0);
        wheel.setRumble(RumbleType.kRightRumble, 0);
    }
}
