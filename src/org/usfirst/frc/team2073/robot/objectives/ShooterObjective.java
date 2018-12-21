package org.usfirst.frc.team2073.robot.objectives;

import com.team2073.common.dev.simulation.subsys.DevShooterSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterSubsystem.ShooterAngle;
import com.team2073.common.objective.AbstractTypedObjective;
import com.team2073.common.objective.StatusChecker;

public class ShooterObjective extends AbstractTypedObjective<ShooterAngle> {

    protected DevShooterSubsystem shooter;

    public ShooterObjective(DevShooterSubsystem shooter, ShooterAngle desiredState) {
        super(desiredState);
        this.shooter = shooter;
    }

    @Override
    public boolean isFinished() {
        return shooter.isAtAngle(getDesiredState());
    }

    @Override
    public void interrupt() {
        shooter.interrupt();
    }

    @Override
    protected StatusChecker start() {
        return shooter.moveToAngle(getDesiredState());
    }
}