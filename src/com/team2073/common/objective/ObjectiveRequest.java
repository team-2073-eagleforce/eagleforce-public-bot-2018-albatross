package com.team2073.common.objective;

public class ObjectiveRequest {

	final Objective requestedObjective;
	ObjectiveStatus status = ObjectiveStatus.QUEUED;

	public ObjectiveRequest(Objective requestedObjective) {
		this.requestedObjective = requestedObjective;
	}

	boolean isDenied() {
		return status == ObjectiveStatus.DENIED;
	}

	public boolean isExecuting() {
		return status == ObjectiveStatus.EXECUTING;
	}

	boolean isQueued() {
		return status == ObjectiveStatus.QUEUED;
	}

	boolean isComplete() {
		return status == ObjectiveStatus.COMPLETED;
	}

	protected boolean isInterrupted() {
		return status == ObjectiveStatus.INTERRUPTED;
	}

	/** Returns true if the command has completed or was interrupted 
	 * (Checks both {@link #isComplete()} and {@link #isInterrupted()}). */
	public boolean isFinished() {
		return isComplete() || isInterrupted();
	}

	void setInterrupted() {
		status = ObjectiveStatus.INTERRUPTED;
	}

}