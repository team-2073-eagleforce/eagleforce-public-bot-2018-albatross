package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;

public class IntakeRightCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private IntakeSideRollerSubsystem intakeSide;

    @Inject
    public IntakeRightCommand(IntakeSideRollerSubsystem intakeSide) {
        this.intakeSide = intakeSide;
        requires(intakeSide);
    }

    @Override
    protected void executeDelegate() {
        intakeSide.rightMotorStart();
    }

    @Override
    protected void endDelegate() {
        intakeSide.rightMotorStop();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
