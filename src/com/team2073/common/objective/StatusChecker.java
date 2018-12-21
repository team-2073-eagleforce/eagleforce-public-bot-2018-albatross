package com.team2073.common.objective;

public class StatusChecker {
	
	private boolean complete = false;

	boolean isComplete() {
		return complete;
	}

	public void complete() {
		this.complete = true;
	}

}
