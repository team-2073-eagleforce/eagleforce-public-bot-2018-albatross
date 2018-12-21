package com.team2073.common.objective;

public class PreconditionMapping {
	
	public static PreconditionMapping create(ObjectivePrecondition precondition, Objective resolution) {
		return new PreconditionMapping(precondition, resolution);
	}

	private ObjectivePrecondition precondition;
	private Objective resolution;

	PreconditionMapping(ObjectivePrecondition precondition, Objective resolution) {
		this.precondition = precondition;
		this.resolution = resolution;
	}
	
	/** Shorthand for <code>getPrecondition().isSafe()</code> */
	boolean isSafe() {
		return precondition.isSafe();
	}
	
	ObjectivePrecondition getPrecondition() {
		return precondition;
	}

	Objective getResolution() {
		return resolution;
	}

}
