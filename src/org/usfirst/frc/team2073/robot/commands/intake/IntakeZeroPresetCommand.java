package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

import javax.annotation.PostConstruct;

public class IntakeZeroPresetCommand extends AbstractLoggingCommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakePivotSubsystem intake;
    @Inject
    private CommandFactory commandFactory;

    @Inject
    public IntakeZeroPresetCommand(IntakePivotSubsystem intake) {
        this.intake = intake;
        requires(intake);
        setInterruptible(false);
        intake.setZeroingPIDGains();
    }

    @PostConstruct
    public void init() {
        addSequential(commandFactory.createRightIntakePresetCommand(210, false), .6);
        addParallel(commandFactory.createLeftIntakePresetCommand(-50, true));
        addSequential(new WaitCommand(.4));
        addParallel(commandFactory.createRightIntakePresetCommand(-50, true));
    }

    @Override
    protected boolean isFinishedDelegate() {
        return intake.getRightLimit();// && intake.getLeftLimit();
    }
}
