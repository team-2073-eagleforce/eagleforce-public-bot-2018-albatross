package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterPivotSubsystem;

public class ShooterPivotFrontToBackCommand extends AbstractLoggingCommand {
    private final ShooterPivotSubsystem shooterPivot;

    @Inject
    public ShooterPivotFrontToBackCommand(ShooterPivotSubsystem shooterPivot) {
        this.shooterPivot = shooterPivot;
        requires(shooterPivot);
    }

    @Override
    protected void executeDelegate() {
        shooterPivot.processMotionProfiling();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return shooterPivot.isMotionProfilingFinished();
    }

    @Override
    protected void endDelegate() {
        shooterPivot.stopMotionProfiling();
        shooterPivot.setBrakeMode();
    }

}
