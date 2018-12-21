package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.buttons.Trigger;

import java.util.Arrays;
import java.util.List;

public class MultiTrigger extends Trigger {
    private final List<Trigger> triggers;

    private MultiTrigger(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public MultiTrigger(Trigger... triggers) {
        this(Arrays.asList(triggers));
    }

    @Override
    public boolean get() {
        return triggers.stream().allMatch(Trigger::get);
    }
}
