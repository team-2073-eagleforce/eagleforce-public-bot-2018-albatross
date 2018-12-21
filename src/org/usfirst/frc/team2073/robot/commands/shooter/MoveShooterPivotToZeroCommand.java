package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class MoveShooterPivotToZeroCommand extends AbstractLoggingCommand {
    private final ShooterPivotSubsystem shooterPivot;

    @Inject
    public MoveShooterPivotToZeroCommand(ShooterPivotSubsystem shooterPivot) {
        this.shooterPivot = shooterPivot;
        requires(shooterPivot);
    }

    @Override
    protected void executeDelegate() {
        shooterPivot.pidAngularSetPoint(0);
    }

    @Override
    protected boolean isFinishedDelegate() {
        System.out.println(shooterPivot.isAtZeroSensor());
        return shooterPivot.isAtZeroSensor();
    }

    @Override
    protected void endDelegate() {
        shooterPivot.stopMotor();
    }

}
