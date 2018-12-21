package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class DigitalInputTrigger extends Trigger {
    private final DigitalInput digitalInput;
    private final boolean inverted;

    public DigitalInputTrigger(DigitalInput digitalInput, boolean inverted) {
        this.digitalInput = digitalInput;
        this.inverted = inverted;
    }

    @Override
    public boolean get() {
        boolean value = digitalInput.get();
        return inverted != value;
    }
}
