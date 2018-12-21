package org.usfirst.frc.team2073.robot.commands.shooter;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;

import javax.annotation.PostConstruct;

public class ShooterPivotZeroThenHold extends AbstractLoggingCommandGroup {

    @Inject
    private ShooterPivotGarretZeroCommand garretZero;

    @PostConstruct
    public void init() {
        addSequential(garretZero);
    }
}
