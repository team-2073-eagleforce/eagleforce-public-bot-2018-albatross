package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class ShooterPivotSetpointCommand extends AbstractLoggingCommand {
    private final ShooterPivotSubsystem shooterPivot;
    private double setPoint;

    @Inject
    public ShooterPivotSetpointCommand(ShooterPivotSubsystem shooterPivot, @Assisted double setPoint) {
        this.shooterPivot = shooterPivot;
        this.setPoint = setPoint;
        requires(shooterPivot);
    }

    @Override
    protected void initializeDelegate() {
        shooterPivot.pidAngularSetPoint(setPoint);
    }

    @Override
    protected void executeDelegate() {
        shooterPivot.periodicPID();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return shooterPivot.hasReachedSetpoint(setPoint);
    }

}
