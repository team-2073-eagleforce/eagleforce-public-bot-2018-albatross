package com.team2073.common.dev.cmd;

import org.usfirst.frc.team2073.robot.temp.subsys.DevSubsystemCoordinatorImpl;

import com.google.inject.Inject;
import com.team2073.common.objective.ObjectiveRequest;

public class DevShooterToFrontUpCommand extends DevAbstractObjectiveCommand {

	@Inject
	public DevShooterToFrontUpCommand(DevSubsystemCoordinatorImpl coordinator) {
		super(coordinator);
	}

	@Override
	protected ObjectiveRequest initializeObjective() {
		return getCoordinator().shooterToFrontUp();
	}
	
}
