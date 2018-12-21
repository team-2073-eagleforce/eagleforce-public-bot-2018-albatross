package org.usfirst.frc.team2073.robot.objectives;

import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.objective.AbstractTypedObjective;
import com.team2073.common.objective.StatusChecker;

public class ElevatorObjective extends AbstractTypedObjective<ElevatorHeight> {

    protected DevElevatorSubsystem elevator;

    public ElevatorObjective(DevElevatorSubsystem elevator, ElevatorHeight desiredState) {
        super(desiredState);
        this.elevator = elevator;
    }

    @Override
    public StatusChecker start() {
        return elevator.moveToHeight(getDesiredState());
    }

    @Override
    public void interrupt() {
        elevator.interrupt();
    }
}
