package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingInstantCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;

public class StopSideIntakeCommand extends AbstractLoggingInstantCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakeSideRollerSubsystem intakeSide;

    @Inject
    public StopSideIntakeCommand(IntakeSideRollerSubsystem intakeSide) {
        this.intakeSide = intakeSide;
        requires(intakeSide);
    }

    @Override
    protected void initializeDelegate() {
        intakeSide.motorStop();
    }
}
