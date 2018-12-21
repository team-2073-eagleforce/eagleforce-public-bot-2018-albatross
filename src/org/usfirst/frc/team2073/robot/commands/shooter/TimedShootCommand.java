package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class TimedShootCommand extends AbstractLoggingCommand {
    private final ShooterSubsystem shooter;

    @Inject
    public TimedShootCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        requires(shooter);
    }

    @Override
    protected void initializeDelegate() {
        shooter.motorForward();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

    @Override
    protected void endDelegate() {
        shooter.motorStop();
    }


}
