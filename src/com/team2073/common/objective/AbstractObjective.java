package com.team2073.common.objective;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Preston Briggs
 *
 * @param <T> The enum defining the state.
 */
public abstract class AbstractObjective implements Objective {

	private List<PreconditionMapping> preconditionList = new ArrayList<>();
	
	/**
	 * Run through this {@link Objective}'s precondition checks to see if there
	 * is anything blocking it from executing. If a precondition fails it will return
	 * the Objective that must be ran to fix the problem so this Objective can run. 
	 * Although multiple preconditions might fail, this will only return the first
	 * one. This method should be called repeatedly until it returns null. Each Objective 
	 * returned should be resolved before calling this method again. 
	 * 
	 * @return The {@link Objective} that needs to be completed before this {@link Objective}
	 * 
	 */
	@Override
	public Objective checkPreconditions() {
		for (PreconditionMapping precondition : preconditionList) {
			if(!precondition.isSafe()) {
				System.out.println("Precondition failed: " + precondition.getPrecondition());
				return precondition.getResolution();
			}
		}
		
		return null;
	}
	
	public void add(ObjectivePrecondition precondition, Objective resolution) {
		add(new PreconditionMapping(precondition, resolution));
	}
	
	public void add(PreconditionMapping precondition) {
		preconditionList.add(precondition);
	}
	
	@Override
	public abstract void execute();
	
	@Override
	public abstract void interrupt();
	
	@Override
	public abstract boolean isFinished();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

}
