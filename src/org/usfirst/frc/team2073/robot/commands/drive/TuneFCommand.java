package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class TuneFCommand extends AbstractLoggingCommand {
    private final DrivetrainSubsystem drivetrain;
    private double startingGyro = 0;

    @Inject
    public TuneFCommand(DrivetrainSubsystem drivetrain) {
        this.drivetrain = drivetrain;
    }

    @Override
    protected void initializeDelegate() {
        startingGyro = drivetrain.getGyroAngle();
    }

    @Override
    protected void executeDelegate() {
        drivetrain.adjustF(startingGyro);
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
