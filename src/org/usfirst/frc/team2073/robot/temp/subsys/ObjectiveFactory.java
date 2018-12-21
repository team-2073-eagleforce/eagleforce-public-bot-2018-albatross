package org.usfirst.frc.team2073.robot.temp.subsys;

import com.google.inject.Inject;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.dev.simulation.subsys.DevShooterSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterSubsystem.ShooterAngle;
import com.team2073.common.objective.ObjectivePrecondition;
import org.usfirst.frc.team2073.robot.objectives.ElevatorObjective;
import org.usfirst.frc.team2073.robot.objectives.ShooterObjective;

import javax.annotation.PostConstruct;

public class ObjectiveFactory {

    // Subsystems
    @Inject
    private DevShooterSubsystem shooter;
    @Inject
    private DevElevatorSubsystem elevator;

    // Objectives
    private ElevatorObjective elevatorToMax;
    private ElevatorObjective elevatorToPivot;
    private ElevatorObjective elevatorToSwitch;
    private ElevatorObjective elevatorToZero;

    private ShooterObjective shooterToFrontStraight;
    private ShooterObjective shooterToFrontUp;
    private ShooterObjective shooterToBackUp;
    private ShooterObjective shooterToBackStraight;

    // Preconditions
    private final ObjectivePrecondition elevatorCanMoveBelowBar = ObjectivePrecondition.named("elevatorCanMoveBelowBar",
            () -> !shooter.isPivotBack());
    private final ObjectivePrecondition canPivotForwards = ObjectivePrecondition.named("canPivotForwards",
            () -> !(shooter.isPivotBack() && !elevator.isAtOrAboveHeight(ElevatorHeight.PIVOT)));
    private final ObjectivePrecondition canPivotBackwards = ObjectivePrecondition.named("canPivotBackwards",
            () -> {
                if (shooter.isPivotBack()) {
                    return true;
                } else return elevator.isAtHeight(ElevatorHeight.PIVOT);
            }
    );


    public ObjectiveFactory() {
    }

    /**
     * Non Dependency-injection constructor
     */
    public ObjectiveFactory(DevShooterSubsystem shooter, DevElevatorSubsystem elevator) {
        this.shooter = shooter;
        this.elevator = elevator;
        init();
    }

    @PostConstruct
    public void init() {
        // Create Objectives
        elevatorToMax = new ElevatorObjective(elevator, ElevatorHeight.MAX);
        elevatorToPivot = new ElevatorObjective(elevator, ElevatorHeight.PIVOT);
        elevatorToSwitch = new ElevatorObjective(elevator, ElevatorHeight.SWITCH);
        elevatorToZero = new ElevatorObjective(elevator, ElevatorHeight.ZERO);

        shooterToFrontStraight = new ShooterObjective(shooter, ShooterAngle.FORWARD_STRAIGHT);
        shooterToFrontUp = new ShooterObjective(shooter, ShooterAngle.FORWARD_UP);
        shooterToBackStraight = new ShooterObjective(shooter, ShooterAngle.BACKWARD);

        // Add preconditions
        elevatorToMax.add(elevatorCanMoveBelowBar, shooterToFrontUp);
        elevatorToSwitch.add(elevatorCanMoveBelowBar, shooterToFrontUp);
        elevatorToZero.add(elevatorCanMoveBelowBar, shooterToFrontUp);
        shooterToFrontStraight.add(canPivotForwards, elevatorToPivot);
        shooterToFrontUp.add(canPivotForwards, elevatorToPivot);
        shooterToBackStraight.add(canPivotBackwards, elevatorToPivot);
    }


    public ElevatorObjective getElevatorToMax() {
        return elevatorToMax;
    }

    public ElevatorObjective getElevatorToPivot() {
        return elevatorToPivot;
    }

    public ElevatorObjective getElevatorToSwitch() {
        return elevatorToSwitch;
    }

    public ElevatorObjective getElevatorToZero() {
        return elevatorToZero;
    }

    public ShooterObjective getShooterToFrontStraight() {
        return shooterToFrontStraight;
    }

    public ShooterObjective getShooterToFrontUp() {
        return shooterToFrontUp;
    }

    public ShooterObjective getShooterToBackUp() {
        return shooterToBackUp;
    }

    public ShooterObjective getShooterToBackStraight() {
        return shooterToBackStraight;
    }
}
