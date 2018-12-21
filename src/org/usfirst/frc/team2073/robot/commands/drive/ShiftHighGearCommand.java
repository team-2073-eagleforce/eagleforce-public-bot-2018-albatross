package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class ShiftHighGearCommand extends AbstractLoggingCommand {
    private DrivetrainSubsystem drivetrain;

    @Inject
    public ShiftHighGearCommand(DrivetrainSubsystem drivetrain) {
        this.drivetrain = drivetrain;
    }

    @Override
    protected void initializeDelegate() {
    }

    @Override
    protected void executeDelegate() {
        drivetrain.shiftHighGear();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }

    @Override
    protected void endDelegate() {
        drivetrain.shiftLowGear();
    }
}
