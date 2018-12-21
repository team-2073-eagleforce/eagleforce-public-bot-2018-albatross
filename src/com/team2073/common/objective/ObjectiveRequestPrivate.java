package com.team2073.common.objective;

public class ObjectiveRequestPrivate extends ObjectiveRequest {

	ObjectiveRequestPrivate(Objective requestedObjective) {
		super(requestedObjective);
	}

	public void execute() {
		status = ObjectiveStatus.EXECUTING;
		requestedObjective.execute();
	}
	
	public void interrupt() {
		status = ObjectiveStatus.INTERRUPTED;
		requestedObjective.interrupt();
	}

	void setComplete() {
		status = ObjectiveStatus.COMPLETED;
	}
	
	public void setDenied() {
		status = ObjectiveStatus.DENIED;
	}
	
	// Getter/setters
	// ============================================================
	Objective getRequestedObjective() {
		return requestedObjective;
	}

	// Object overrides
	// ============================================================
	@Override
	public String toString() {
		return "ObjectiveRequest [" + requestedObjective + " : " + status + "]";
	}
}
