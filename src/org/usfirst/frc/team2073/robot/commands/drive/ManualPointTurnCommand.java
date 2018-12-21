package org.usfirst.frc.team2073.robot.commands.drive;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.team2073.common.command.AbstractLoggingCommand;
import edu.wpi.first.wpilibj.Joystick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

public class ManualPointTurnCommand extends AbstractLoggingCommand {
    private final DrivetrainSubsystem drivetrain;
    private final Joystick wheel;

    @Inject
    public ManualPointTurnCommand(DrivetrainSubsystem drivetrain, @Named("joystick") Joystick joystick,
                                  @Named("wheel") Joystick wheel) {

        this.drivetrain = drivetrain;
        this.wheel = wheel;
        requires(drivetrain);
    }

    @Override
    protected void executeDelegate() {
        drivetrain.pointTurn(drivetrain.adjustedTurn(wheel.getX()));
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
