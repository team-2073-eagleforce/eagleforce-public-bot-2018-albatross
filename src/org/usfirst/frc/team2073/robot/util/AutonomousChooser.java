package org.usfirst.frc.team2073.robot.util;

import com.google.inject.Inject;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team2073.robot.Robot.Alignment;
import org.usfirst.frc.team2073.robot.commands.auto.DriveForwardAndZeroCommandGroup;
import org.usfirst.frc.team2073.robot.commands.auto.DriveForwardOnly;
import org.usfirst.frc.team2073.robot.commands.auto.center.CenterLeftSwitch;
import org.usfirst.frc.team2073.robot.commands.auto.center.CenterRightSwitch;
import org.usfirst.frc.team2073.robot.commands.auto.left.LeftLeftSwitchLeftScale;
import org.usfirst.frc.team2073.robot.commands.auto.right.RightRightSwitchRightScale;
import org.usfirst.frc.team2073.robot.util.GameDataParser.Side;

public class AutonomousChooser {
    @Inject
    private CenterLeftSwitch centerLeftSwitch;
    @Inject
    private CenterRightSwitch centerRightSwitch;
    @Inject
    private DriveForwardAndZeroCommandGroup driveForwardAndZero;
    @Inject
    private LeftLeftSwitchLeftScale leftSideLeftSwitch;
    @Inject
    private RightRightSwitchRightScale rightSideRightSwitch;
    @Inject
    private DriveForwardOnly driveForwardOnly;

    public Command autonomousSelector(Alignment alignment, Side switchSide, Side scaleSide) {
        switch (alignment) {
            case CENTER:
                switch (switchSide) {
                    case LEFT:
                        return centerLeftSwitch;
                    case RIGHT:
                        return centerRightSwitch;
                }
            case LEFT:
                switch (switchSide) {
                    case LEFT:
                        switch (scaleSide) {
                            case LEFT:
                                return leftSideLeftSwitch;
                            case RIGHT:
                                return leftSideLeftSwitch;
                        }
                    case RIGHT:
                        switch (scaleSide) {
                            case LEFT:
                                return driveForwardOnly;
                            case RIGHT:
                                return driveForwardOnly;
                        }
                }
            case RIGHT:
                switch (switchSide) {
                    case LEFT:
                        switch (scaleSide) {
                            case LEFT:
                                return driveForwardOnly;
                            case RIGHT:
                                return driveForwardOnly;
                        }
                    case RIGHT:
                        switch (scaleSide) {
                            case LEFT:
                                return rightSideRightSwitch;
                            case RIGHT:
                                return rightSideRightSwitch;
                        }
                }
        }
        return driveForwardAndZero;
    }
}
