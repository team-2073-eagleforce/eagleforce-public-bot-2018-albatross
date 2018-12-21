package com.team2073.common.motionprofiling;

import java.util.List;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.team2073.common.threading.InterruptibleRunnable;

public class TrajectoryPointPusher implements InterruptibleRunnable {
	private final MotionProfileHelper motorHelper;
	private final List<TrajectoryPoint> trajPointList;

	public TrajectoryPointPusher(MotionProfileHelper motorHelper, List<TrajectoryPoint> trajPointList/*,  Command command, Subsystem subsystem*/) {
		this.motorHelper = motorHelper;
		this.trajPointList = trajPointList;
	}
	
	@Override
	public void run() {
		motorHelper.pushPoints(trajPointList);
	}

	@Override
	public void interrupt() {
	}
	
}