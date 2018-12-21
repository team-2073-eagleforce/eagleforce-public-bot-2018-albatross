package com.team2073.common.dev.simulation.subsys;

import com.team2073.common.dev.simulation.io.FakeTalon;
import com.team2073.common.objective.StatusChecker;

import edu.wpi.first.wpilibj.command.Subsystem;

public class DevElevatorSubsystem extends Subsystem {
	
	public enum ElevatorHeight {
		ZERO(0, -1, 1),
//		BETWEEN_ZERO_AND_SWITCH,
		SWITCH(1, 8, 9),
//		BETWEEN_SWITCH_AND_PIVOT,
		PIVOT(2, 18, 19),
//		BETWEEN_PIVOT_AND_MAX,
		MAX(3, 28, 29);

		private int index;
		private double lowerBound;
		private double midPoint;
		private double upperBound;
		
		ElevatorHeight(int index, double lowerBound, double upperBound) {
			this.index = index;
			this.lowerBound = lowerBound;
			this.midPoint = (lowerBound + upperBound) / 2;
			this.upperBound = upperBound;
		}

		public int getIndex() {
			return index;
		}
		
		public double getLowerBound() {
			return lowerBound;
		}
		
		public double getUpperBound() {
			return upperBound;
		}

		public double getMidPoint() {
			return midPoint;
		}
		
		public boolean withinBounds(double point) {
			return point >= lowerBound && point <= upperBound;
		}

		public String toStringDetailed() {
			return super.toString() + "{" + lowerBound + " -> " + midPoint + " <- " + upperBound + "}";
		}
	}
	
	private ElevatorHeight goalHeight = ElevatorHeight.ZERO;
	private StatusChecker goalStatus = null;
	private FakeTalon talon = new FakeTalon();

	@Override
	protected void initDefaultCommand() {
	}
	
	@Override
	public void periodic() {
		if (goalHeight == null) {
			return;
		}
		
		double currHeight = getCurrentHeight();
		
		if(goalHeight.withinBounds(currHeight)) {
			System.out.printf("DevElevatorSubsystem: Reached goal height [%s]. Current height [%s]\n"
					, goalHeight.toStringDetailed(), currHeight);
			stop();
			return;
		}
		
		if(currHeight < goalHeight.lowerBound) {
			moveUp();
		} else if(currHeight > goalHeight.upperBound) {
			moveDown();
		} else {
			System.out.println("Your code be broken yo!");
		}
	}

	// Public control
	// ============================================================
	public void interrupt() {
		stop();
	}

	public StatusChecker moveToHeight(ElevatorHeight height) {
		goalHeight = height;
		goalStatus = new StatusChecker();
		return goalStatus;
	}

	// Public informational
	// ============================================================
	public boolean isMovingUp(ElevatorHeight comparingState) {
		return getCurrentHeight() < comparingState.index;
	}

	public boolean isMovingDown(ElevatorHeight comparingState) {
		return getCurrentHeight() > comparingState.index;
	}

	public boolean isAtHeight(ElevatorHeight height) {
		return height.withinBounds(getCurrentHeight());
	}

	public boolean isAtOrAboveHeight(ElevatorHeight height) {
		return getCurrentHeight() >= height.lowerBound;
	}
	
	public double getCurrentHeight() {
		return talon.position;
	}

	// Private motor control
	// ============================================================
	private void stop() {
		talon.set(0);
		goalHeight = null;
		if(goalStatus != null) {
			System.out.println("DevElevatorSubsystem cmopleting StatusChecker");
			goalStatus.complete();
			goalStatus = null;
		}
	}
	
	private void moveUp() {
		System.out.printf("DevElevatorSubsystem: Moving up. [%s] -> [%s]\n", getCurrentHeight(), goalHeight.lowerBound);
		talon.set(1);
	}
	
	private void moveDown() {
		System.out.printf("DevElevatorSubsystem: Moving down. [%s] -> [%s]\n", getCurrentHeight(), goalHeight.upperBound);
		talon.set(-1);
	}

}
