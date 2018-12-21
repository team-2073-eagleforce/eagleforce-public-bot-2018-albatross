package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

public class RightIntakePresetNoHoldCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakePivotSubsystem intake;
    private double angle;
    private boolean isZeroingPID;

    @Inject
    public RightIntakePresetNoHoldCommand(IntakePivotSubsystem intake, @Assisted double angle, @Assisted boolean isZeroingPID) {
        this.intake = intake;
        this.angle = angle;
        this.isZeroingPID = isZeroingPID;
        requires(intake);
    }

    @Override
    protected void initializeDelegate() {
        intake.presetRightIntakePivot(angle, isZeroingPID);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
