package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeBottomRollerSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;

public class IntakeSidesCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakeSideRollerSubsystem intakeSide;

    @Inject
    public IntakeSidesCommand(IntakeSideRollerSubsystem intakeSide, IntakeBottomRollerSubsystem intakeBottom) {
        IntakeBottomRollerSubsystem intakeBottom1 = intakeBottom;
        this.intakeSide = intakeSide;
        requires(intakeBottom);
        requires(intakeSide);
    }

    @Override
    protected void executeDelegate() {
        intakeSide.motorStart();
    }

    @Override
    protected void endDelegate() {
        intakeSide.motorStop();
        intakeSide.resetPulse();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
