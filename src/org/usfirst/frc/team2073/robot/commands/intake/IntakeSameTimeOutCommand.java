package org.usfirst.frc.team2073.robot.commands.intake;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.CommandFactory;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;

import edu.wpi.first.wpilibj.command.WaitCommand;

public class IntakeSameTimeOutCommand extends AbstractLoggingCommandGroup {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Inject private CommandFactory commandFactotry;

	@Inject
	public IntakeSameTimeOutCommand(IntakePivotSubsystem intake) {
		IntakePivotSubsystem intake1 = intake;
		requires(intake);
		setInterruptible(true);
	}
	
	@PostConstruct
	public void init() {
		addParallel(commandFactotry.createRightIntakePresetCommand(/*190*/220, false));
		addParallel(commandFactotry.createLeftIntakePresetCommand(/*190*/220, false));
	}

	@Override
	protected boolean isFinishedDelegate() {
		return false;
	}
}
