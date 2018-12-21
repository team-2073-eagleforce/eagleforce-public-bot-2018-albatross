package com.team2073.common.objective;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSubsystemCoordinator {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ArrayDeque<ObjectiveRequestPrivate> objectiveRequestStack = new ArrayDeque<>();
	private boolean waiting = false;
	
	/**
	 * Interrupts all {@link Objective}s and clears the stack. Used internally on errors
	 * to 'reset' and can also be used externally to cancel all Objectives (for example 
	 * on disabled).
	 */
	public void reset() {
		System.out.println("Clearing Objective stack");
		for (ObjectiveRequestPrivate request : objectiveRequestStack) {
			System.out.println("Interrupting " + request.getRequestedObjective());
			request.getRequestedObjective().interrupt();
		}
		objectiveRequestStack.clear();
	}

	public void periodic() {
		ObjectiveRequestPrivate currReq = objectiveRequestStack.peekLast();
		try {
			periodicInternal(currReq);
		} catch (Exception e) {
			logger.error("Exception occurred processing Objective [{}]. Stack [{}].", currReq, printStack(), e);
			objectiveRequestStack.clear();
		}
	}
	
	private void periodicInternal(ObjectiveRequestPrivate currReq) throws Exception {
		
		if(currReq == null) {
			if(!waiting) {
				System.out.println("DevSubsystemCoordinatorImpl: No objectives to process.");
				System.out.println();
				System.out.println();
				System.out.println("Waiting...");
				System.out.println();
				System.out.println();
				waiting = true;
			}
			return;
		}
		waiting = false;
		
		// Check interruptions
		if(currReq.isInterrupted()) {
			System.out.printf("Interrupting [%s].\n", currReq);
			currReq.getRequestedObjective().interrupt();
			pop(currReq);
			return;
		}
		
		// Check completed
		if (currReq.isComplete() || currReq.isDenied()) {
			pop(currReq);
			return;
		}
		
		// Check preconditions
		Objective blockingObjective = currReq.getRequestedObjective().checkPreconditions();
		
		if(blockingObjective != null) {
			System.out.printf("[%s] is blocking [%s]\n", blockingObjective, currReq.getRequestedObjective());
			if(isCircularQueuing(blockingObjective)) {
				reset();
				return;
			}
			queuePreconditionResolution(blockingObjective);
			return;
		}

		// If hasn't been executed yet, execute
		if(currReq.isQueued()) {
			System.out.printf("Executing [%s]\n", currReq);
			currReq.execute();
			return;
		}
		
		// Finish
		if(currReq.getRequestedObjective().isFinished()) {
			System.out.printf("Completing [%s]\n", currReq);
			currReq.setComplete();
			return;
		}
		
	}
	
	private void pop(ObjectiveRequest completedObjReq) {
		ObjectiveRequest pop = objectiveRequestStack.pollLast();
		System.out.println("Removed objective: " + pop);
		System.out.println("Stack: [" + printStack() + "]");
		if(completedObjReq != pop)
			System.out.printf("WARNING: Popped ObjectiveRequest [%s] did not match the completed ObjectiveRequest [%s]\n", pop, completedObjReq);
	}
	
	private boolean isCircularQueuing(Objective blockingObjective) {
		for (ObjectiveRequestPrivate objective : objectiveRequestStack) {
			System.out.printf("Comparing [%s] to [%s].\n", blockingObjective, objective.getRequestedObjective());
			if(blockingObjective.equals(objective.getRequestedObjective())) {
				System.out.println();
				System.out.println();
				System.out.println("WARN: Objectives match!");
				System.out.println();
				System.out.println();
				return true;
			}
		}
		
		return false;
	}

	private ObjectiveRequest queuePreconditionResolution(Objective objective) {
		ObjectiveRequestPrivate objectiveRequestPrivate = new ObjectiveRequestPrivate(objective);
		objectiveRequestStack.add(objectiveRequestPrivate);
		System.out.println("Queued new precondition resolution: " + objective);
		System.out.println("Stack: [" + printStack() + "]");
		return objectiveRequestPrivate;
	}
	
	protected ObjectiveRequest queue(Objective objective) {
		ObjectiveRequestPrivate objectiveRequestPrivate = new ObjectiveRequestPrivate(objective);
		objectiveRequestStack.push(objectiveRequestPrivate);
		System.out.println("Queued new objective: " + objective);
		System.out.println("Stack: [" + printStack() + "]");
		return objectiveRequestPrivate;
	}

	// Subclass callbacks
	// ============================================================
	/**
	 * Subclasses may override this method to do some reporting on errors such as 
	 * vibrating a controller giving the driver feedback that something went wrong.
	 * 
	 * @see #onError(Exception)
	 */
	protected void onError(ErrorType error) {
		
	}
	
	/**
	 * Subclasses may override this method to do some reporting on errors such as 
	 * vibrating a controller giving the driver feedback that something went wrong.
	 * 
	 * @see #onError(ErrorType)
	 */
	protected void onError(Exception exception) {
		
	}


	// Helpers
	// ============================================================
	private String printStack() {
		return objectiveRequestStack.stream().map(objReq -> objReq.getRequestedObjective().toString()).collect(Collectors.joining(" -> "));
	}
	
	public enum ErrorType {
		/** An {@link Objective}'s {@link ObjectivePrecondition} failed and the resolution was the same as 
		 * the initial Objective. This will cause an infinite loop of Objective queuing so instead, the
		 * Objective stack is cleared. This will cause any currently running Objectives to end abruptly, first
		 * calling interrupt on the Objective. */
	}
}
