package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;

public class OuttakeSidesCommand extends AbstractLoggingCommand {
    private final IntakeSideRollerSubsystem intakeSide;

    @Inject
    public OuttakeSidesCommand(IntakeSideRollerSubsystem intakeSide) {
        this.intakeSide = intakeSide;
        requires(intakeSide);
        setInterruptible(true);

    }

    @Override
    protected void executeDelegate() {
        intakeSide.reverseMotorStart();

    }

    @Override
    protected void endDelegate() {
        intakeSide.motorStop();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
