package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class StopShooterPivotCommand extends AbstractLoggingCommand {

    @Inject
    public StopShooterPivotCommand(ShooterPivotSubsystem shooterPivot) {
        ShooterPivotSubsystem shooterPivot1 = shooterPivot;
        requires(shooterPivot);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
