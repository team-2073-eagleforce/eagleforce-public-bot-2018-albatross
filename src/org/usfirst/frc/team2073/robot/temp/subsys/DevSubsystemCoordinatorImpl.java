package org.usfirst.frc.team2073.robot.temp.subsys;

import com.google.inject.Inject;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterSubsystem;
import com.team2073.common.objective.AbstractSubsystemCoordinator;
import com.team2073.common.objective.ObjectiveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSubsystemCoordinatorImpl extends AbstractSubsystemCoordinator {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private ObjectiveFactory objFactory;

    // Constructors
    // ============================================================
    public DevSubsystemCoordinatorImpl() {
    }

    /**
     * Non Dependency-injection constructor
     */
    public DevSubsystemCoordinatorImpl(DevElevatorSubsystem elevator, DevShooterSubsystem shooter) {
        this.objFactory = new ObjectiveFactory(shooter, elevator);
    }

    // Public methods
    // ============================================================
    public ObjectiveRequest elevatorToZero() {
        return queue(objFactory.getElevatorToZero());
    }

    public ObjectiveRequest elevatorToSwitch() {
        return queue(objFactory.getElevatorToSwitch());
    }

    public ObjectiveRequest elevatorToPivot() {
        return queue(objFactory.getElevatorToPivot());
    }

    public ObjectiveRequest elevatorToMax() {
        return queue(objFactory.getElevatorToMax());
    }

    public ObjectiveRequest shooterToFrontStraight() {
        return queue(objFactory.getShooterToFrontStraight());
    }

    public ObjectiveRequest shooterToFrontUp() {
        return queue(objFactory.getShooterToFrontUp());
    }

    public ObjectiveRequest shooterToBackStraight() {
        return queue(objFactory.getShooterToBackStraight());
    }
}
