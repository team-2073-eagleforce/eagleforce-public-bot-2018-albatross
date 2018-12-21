package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class NotTrigger extends Trigger {
    private final Trigger trigger;

    public NotTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public boolean get() {
        return !trigger.get();
    }
}
