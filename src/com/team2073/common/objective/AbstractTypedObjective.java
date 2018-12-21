package com.team2073.common.objective;

/**
 * An implementation of {@link Objective} that allows specifying a state of type {@link Enum}.
 * This state is generally defined in the subsystem this Objective will interact with.
 * This allows the Objective to easily communicate with the Subsystem using the state enum.
 * 
 * @author Preston Briggs
 *
 * @param <T> The enum defining the various states a subsystem may be in
 */
public abstract class AbstractTypedObjective<T extends Enum<T>> extends AbstractObjective {

	private final T desiredState;
	private StatusChecker status;
	
	public AbstractTypedObjective(T desiredState) {
		this.desiredState = desiredState;
	}

	protected T getDesiredState() {
		return desiredState;
	}

	@Override
	public void execute() {
		status = start();
	}

	@Override
	public boolean isFinished() {
		return status.isComplete();
	}
	
	@Override
	public String toString() {
		return desiredState.name();
	}
	
	protected abstract StatusChecker start();


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desiredState == null) ? 0 : desiredState.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractTypedObjective other = (AbstractTypedObjective) obj;
		if (desiredState == null) {
            return other.desiredState == null;
		} else return desiredState.equals(other.desiredState);
    }
	
}
