package org.usfirst.frc.team2073.robot.commands;

import org.usfirst.frc.team2073.robot.commands.drive.*;
import org.usfirst.frc.team2073.robot.commands.drive.MoveMpCommand.Direction;
import org.usfirst.frc.team2073.robot.commands.elevator.ElevatorSetPointCommand;
import org.usfirst.frc.team2073.robot.commands.intake.*;
import org.usfirst.frc.team2073.robot.commands.intake.IntakePresetCommand.HoldType;
import org.usfirst.frc.team2073.robot.commands.intake.IntakePresetCommand.PIDType;
import org.usfirst.frc.team2073.robot.commands.intake.IntakePresetCommand.Side;
import org.usfirst.frc.team2073.robot.commands.shooter.ShooterPivotSetpointCommand;

public interface CommandFactory {
    MoveMpCommand createMoveMpCommand(Direction direction, double distance);

    MoveForwardMpCommand createMoveForwardMpCommand(double distance);

    PointTurnMpCommand createPointTurnMpCommand(double angle);

    MoveBackwardMpCommand createMoveBackwardMpCommand(double distance);

    ShooterPivotSetpointCommand createShooterPivotSetpointCommand(double angle);

    ElevatorSetPointCommand createElevatorSetPointCommand(double height);

    IntakePresetCommand createIntakePresetCommand(Side side, double angle, HoldType holdType, PIDType pidType);

    LeftIntakePresetCommand createLeftIntakePresetCommand(double angle, boolean isZeroingPID);

    RightIntakePresetCommand createRightIntakePresetCommand(double angle, boolean isZeroingPID);

    RightIntakePresetNoHoldCommand createRightIntakePresetNoHoldCommand(double angle, boolean isZeroingPID);

    LeftIntakePresetNoHoldCommand createLeftIntakePresetNoHoldCommand(double angle, boolean isZeroingPID);

    MoveForwardPIDCommand createMoveStraightPIDCommand(double linearDistance);

    PointTurnPIDCommand createPointTurnPIDCommand(double angle);

    DriveAndPivotPIDCommand createDriveAndPivotPIDCommand(double distance);

    SetCameraModeCommand createSetCameraModeCommand(int cameraMode);
}
