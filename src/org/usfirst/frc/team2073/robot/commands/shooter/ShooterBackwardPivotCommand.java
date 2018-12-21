package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingInstantCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class ShooterBackwardPivotCommand extends AbstractLoggingInstantCommand {
    private final ShooterPivotSubsystem shooterPivot;

    @Inject
    public ShooterBackwardPivotCommand(ShooterPivotSubsystem shooterPivot) {
        this.shooterPivot = shooterPivot;
        requires(shooterPivot);
    }

    @Override
    protected void initializeDelegate() {
        shooterPivot.spinBackward();
    }
}
