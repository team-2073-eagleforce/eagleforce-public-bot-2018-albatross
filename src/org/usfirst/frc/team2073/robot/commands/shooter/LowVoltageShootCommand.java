package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class LowVoltageShootCommand extends AbstractLoggingCommand {
    private final ShooterSubsystem shooter;

    @Inject
    public LowVoltageShootCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        requires(shooter);
        setInterruptible(false);
    }

    @Override
    protected void initializeDelegate() {
        shooter.lowVoltageShoot();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
