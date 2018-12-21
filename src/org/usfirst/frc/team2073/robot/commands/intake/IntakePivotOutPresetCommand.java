package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

import javax.annotation.PostConstruct;

public class IntakePivotOutPresetCommand extends AbstractLoggingCommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakePivotSubsystem intake;
    @Inject
    private CommandFactory commandFactotry;

    @Inject
    public IntakePivotOutPresetCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        requires(intake);
    }

    @PostConstruct
    public void init() {

        addParallel(commandFactotry.createRightIntakePresetCommand(/*215*/215, false));
        addSequential(new WaitCommand(.25));
        addParallel(commandFactotry.createLeftIntakePresetCommand(/*215*/215, false));
    }

    @Override
    protected boolean isFinishedDelegate() {
        return intake.hasReachedPosition(190);
    }
}
