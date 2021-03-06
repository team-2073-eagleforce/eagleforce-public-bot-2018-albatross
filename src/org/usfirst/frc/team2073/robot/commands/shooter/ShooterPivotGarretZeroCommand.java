package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class ShooterPivotGarretZeroCommand extends AbstractLoggingCommand {
    private final ShooterPivotSubsystem shooterPivot;

    @Inject
    public ShooterPivotGarretZeroCommand(ShooterPivotSubsystem shooterPivot) {
        this.shooterPivot = shooterPivot;
        requires(shooterPivot);
    }

    @Override
    protected void initializeDelegate() {
        shooterPivot.spinForward();
    }


    @Override
    protected void endDelegate() {
        shooterPivot.stopMotor();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return shooterPivot.isAtZeroSensor();
    }
}
