package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

public class DPadTrigger extends Trigger {
    private Joystick controller;
    private double angle;

    public DPadTrigger(Joystick controller, double angle) {
        this.controller = controller;
        this.angle = angle;
    }

    @Override
    public boolean get() {
        return controller.getPOV() == angle;
    }

}
