package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class ZeroShooterPivotCommand extends AbstractLoggingCommand {
    private final ShooterPivotSubsystem shooterPivot;

    @Inject
    public ZeroShooterPivotCommand(ShooterPivotSubsystem shooterPivot) {
        this.shooterPivot = shooterPivot;
        setRunWhenDisabled(true);
    }

    @Override
    protected void initializeDelegate() {
        shooterPivot.zeroEncoder();
    }

    @Override
    protected void executeDelegate() {
        shooterPivot.zeroEncoder();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
