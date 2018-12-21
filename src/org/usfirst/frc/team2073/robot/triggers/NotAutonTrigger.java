package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class NotAutonTrigger extends Trigger {

    public NotAutonTrigger() {
    }

    @Override
    public boolean get() {
        return !DriverStation.getInstance().isAutonomous();
    }

}
