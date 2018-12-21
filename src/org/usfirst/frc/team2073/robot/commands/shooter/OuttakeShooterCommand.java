package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class OuttakeShooterCommand extends AbstractLoggingCommand {
    private final ShooterSubsystem shooter;

    @Inject
    public OuttakeShooterCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
        requires(shooter);
        setInterruptible(false);
    }

    @Override
    protected void initializeDelegate() {
        Timer.delay(.1);
        shooter.motorForward();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
