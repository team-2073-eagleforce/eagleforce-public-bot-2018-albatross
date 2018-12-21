package com.team2073.common.dev.simulation.io;

public class FakeTalon {
	
	public double position;
	
	public FakeTalon() {
	}

	public FakeTalon(double position) {
		this.position = position;
	}
	
	public void set(double speed) {
		position += speed;
	}
}
