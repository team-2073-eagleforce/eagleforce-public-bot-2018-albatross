package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

public class IntakeHoldPresetCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakePivotSubsystem intake;

    @Inject
    public IntakeHoldPresetCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        requires(intake);
    }

    @Override
    protected void initializeDelegate() {
        intake.presetLeftIntakePivot(160/*145*/, false);
        intake.presetRightIntakePivot(165/*141*/, false);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
