package com.team2073.common.objective;

import org.usfirst.frc.team2073.robot.temp.subsys.DevSubsystemCoordinatorImpl;

import com.team2073.common.command.AbstractLoggingCommand;

public abstract class AbstractObjectiveCommand<T extends DevSubsystemCoordinatorImpl> extends AbstractLoggingCommand {

	private T coordinator;
	
	private ObjectiveRequest request = null;
	
	public AbstractObjectiveCommand(T coordinator) {
		this.coordinator = coordinator;
	}

	@Override
	protected void initializeDelegate() {
		request = initializeObjective();
	}
	
	@Override
	protected boolean isFinishedDelegate() {
		return request.isFinished();
	}

	@Override
	protected void endDelegate() {
		request = null;
	}
	
	@Override
	protected void interruptedDelegate() {
		request.setInterrupted();
	}
	
	protected T getCoordinator() {
		return coordinator;
	}
	
	protected abstract ObjectiveRequest initializeObjective();

}
