package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class HallEffect extends Trigger {
    private DigitalInput sensor;

    public HallEffect(DigitalInput sensor) {
        this.sensor = sensor;
    }

    @Override
    public boolean get() {
        return !sensor.get();
    }

}
