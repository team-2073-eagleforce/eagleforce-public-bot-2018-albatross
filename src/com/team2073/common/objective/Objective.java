package com.team2073.common.objective;

public interface Objective {

	void execute();

	void interrupt();

	boolean isFinished();

	Objective checkPreconditions();

}