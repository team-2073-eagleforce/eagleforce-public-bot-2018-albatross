package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

public class ZeroRightIntakeCommand extends AbstractLoggingCommand {
    private final IntakePivotSubsystem intake;

    @Inject
    public ZeroRightIntakeCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        setRunWhenDisabled(true);
    }

    @Override
    protected void initializeDelegate() {
        intake.stopRightPivot();
        intake.zeroRightEncoder();
    }

    @Override
    protected void executeDelegate() {
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
