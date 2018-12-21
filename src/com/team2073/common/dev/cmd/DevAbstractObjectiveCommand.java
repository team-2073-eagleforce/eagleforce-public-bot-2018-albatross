package com.team2073.common.dev.cmd;

import org.usfirst.frc.team2073.robot.temp.subsys.DevSubsystemCoordinatorImpl;

import com.team2073.common.objective.AbstractObjectiveCommand;

abstract class DevAbstractObjectiveCommand extends AbstractObjectiveCommand<DevSubsystemCoordinatorImpl> {

	DevAbstractObjectiveCommand(DevSubsystemCoordinatorImpl coordinator) {
		super(coordinator);
	}
	
}
