package org.usfirst.frc.team2073.robot.commands;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.PowerTakeOffSubsystem;

import javax.annotation.PostConstruct;

public class TogglePTOCommand extends AbstractLoggingCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private PowerTakeOffSubsystem pto;

    @PostConstruct
    private void init() {
        requires(pto);
    }

    @Override
    protected void initializeDelegate() {
        pto.engagePTO();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

    @Override
    protected void endDelegate() {
        pto.disengagePTO();
    }
}
