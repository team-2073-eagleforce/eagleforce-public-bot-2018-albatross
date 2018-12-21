package org.usfirst.frc.team2073.robot.commands;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingInstantCommand;
import edu.wpi.first.wpilibj.Timer;

@Deprecated
public class WaitCommand extends AbstractLoggingInstantCommand {
    private double delayInSeconds;

    @Inject
    public WaitCommand(@Assisted double delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    @Override
    protected void initializeDelegate() {
        Timer.delay(delayInSeconds);
    }
}
