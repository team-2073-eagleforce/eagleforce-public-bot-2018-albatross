package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

public class FollowerIntakeModeCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakePivotSubsystem intake;

    @Inject
    public FollowerIntakeModeCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        requires(intake);
    }

    @Override
    protected void executeDelegate() {
        intake.controlLeftPivot();
        intake.controlRightPivot();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
