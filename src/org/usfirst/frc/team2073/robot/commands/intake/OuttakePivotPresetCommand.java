package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

public class OuttakePivotPresetCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakePivotSubsystem intake;

    @Inject
    public OuttakePivotPresetCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        requires(intake);
        setInterruptible(false);
    }

    @Override
    protected void executeDelegate() {
        intake.presetLeftIntakePivot(200, false);
        intake.presetRightIntakePivot(200, false);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
