package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

public class ZeroLeftIntakeCommand extends AbstractLoggingCommand {
    private final IntakePivotSubsystem intake;

    @Inject
    public ZeroLeftIntakeCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        setRunWhenDisabled(true);
    }

    @Override
    protected void initializeDelegate() {
        intake.stopLeftPivot();
        intake.zeroLeftEncoder();
    }

    @Override
    protected void executeDelegate() {

    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
