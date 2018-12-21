package com.team2073.common.dev.simulation;

import org.usfirst.frc.team2073.robot.temp.subsys.DevSubsystemCoordinatorImpl;

import com.team2073.common.dev.cmd.DevElevatorToMaxCommand;
import com.team2073.common.dev.cmd.DevElevatorToZeroCommand;
import com.team2073.common.dev.cmd.DevShooterToBackStraightCommand;
import com.team2073.common.dev.cmd.DevShooterToFrontStraightCommand;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class SimulationOperatorInterface {
	
	public static final Joystick controller = new Joystick(0);
	private static final JoystickButton a = new JoystickButton(controller, 1);
	private static final JoystickButton b = new JoystickButton(controller, 2);
	private static final JoystickButton x = new JoystickButton(controller, 3);
	private static final JoystickButton y = new JoystickButton(controller, 4);

	public static void init() {
		DevSubsystemCoordinatorImpl subsysCrd = SimulationRobot.subsysCrd;
		
		DevElevatorToZeroCommand elevToZero = new DevElevatorToZeroCommand(subsysCrd);
		DevElevatorToMaxCommand elevToMax = new DevElevatorToMaxCommand(subsysCrd);
		DevShooterToFrontStraightCommand shooterToFront = new DevShooterToFrontStraightCommand(subsysCrd);
		DevShooterToBackStraightCommand shooterToBack = new DevShooterToBackStraightCommand(subsysCrd);
		
		a.whenPressed(elevToZero);
		y.whenPressed(elevToMax);
		x.whenPressed(shooterToFront);
		b.whenPressed(shooterToBack);
	}

}
