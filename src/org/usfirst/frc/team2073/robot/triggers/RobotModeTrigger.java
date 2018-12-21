package org.usfirst.frc.team2073.robot.triggers;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.buttons.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotModeTrigger extends Trigger {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Mode mode;

    public RobotModeTrigger(Mode mode) {
        this.mode = mode;
    }

    @Override
    public boolean get() {
        switch (mode) {
            case DISABLED:
                return RobotState.isDisabled();
            case AUTONOMOUS:
                return RobotState.isAutonomous();
            case TELEOP:
                return RobotState.isOperatorControl();
            case TEST:
                return RobotState.isTest();
            default:
                logger.warn("Invalid robot mode [{}]. Returning false.", mode);
                return false;
        }
    }

    public enum Mode {
        DISABLED, AUTONOMOUS, TELEOP, TEST
    }
}
