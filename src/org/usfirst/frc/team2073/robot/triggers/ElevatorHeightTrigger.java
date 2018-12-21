package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.buttons.Trigger;
import org.usfirst.frc.team2073.robot.subsystems.ElevatorSubsystem;

public class ElevatorHeightTrigger extends Trigger {
    private double height;
    private ElevatorSubsystem elevator;

    public ElevatorHeightTrigger(double height, ElevatorSubsystem elevator) {
        this.height = height;
        this.elevator = elevator;
    }

    @Override
    public boolean get() {
        return elevator.getCurrentHeight() > height;
    }

}
