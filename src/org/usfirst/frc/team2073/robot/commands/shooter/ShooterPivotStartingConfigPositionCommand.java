package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class ShooterPivotStartingConfigPositionCommand extends AbstractLoggingCommand {
    private final ShooterPivotSubsystem shooterPivot;

    @Inject
    public ShooterPivotStartingConfigPositionCommand(ShooterPivotSubsystem shooterPivot) {
        this.shooterPivot = shooterPivot;
        requires(shooterPivot);
    }

    @Override
    protected void initializeDelegate() {
        shooterPivot.pidAngularSetPoint(40);
    }

    @Override
    protected void executeDelegate() {
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

}
