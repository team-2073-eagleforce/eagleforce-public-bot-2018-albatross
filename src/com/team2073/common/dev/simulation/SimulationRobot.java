package com.team2073.common.dev.simulation;

import org.usfirst.frc.team2073.robot.temp.subsys.DevSubsystemCoordinatorImpl;

import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterSubsystem;
import com.team2073.common.robot.AbstractRobotDelegate;

import edu.wpi.first.wpilibj.command.Scheduler;

public class SimulationRobot extends AbstractRobotDelegate {

	public static DevSubsystemCoordinatorImpl subsysCrd;
	
	@Override
	public void robotInit() {
		subsysCrd = new DevSubsystemCoordinatorImpl(new DevElevatorSubsystem(), new DevShooterSubsystem());
		SimulationOperatorInterface.init();
	}
	
	@Override
	public void robotPeriodic() {
		Scheduler.getInstance().run();
		subsysCrd.periodic();
	}
}
