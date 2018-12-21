package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class StopShooterCommand extends AbstractLoggingCommand {
    private final ShooterSubsystem shooter;

    @Inject
    public StopShooterCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        requires(shooter);
    }

    @Override
    protected void initializeDelegate() {
        shooter.motorStop();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
