package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.team2073.common.command.AbstractLoggingCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;

import javax.annotation.PostConstruct;

/**
 * Example usage:
 *
 * <pre>
 * &commat;Inject private CommandFactory commandFactory;
 *
 * commandFactory.createIntakePresetCommand(Side.LEFT, 215, HoldType.HOLD, PIDType.DEFAULT);
 * </pre>
 */
public class IntakePresetCommand extends AbstractLoggingCommand {
    @Inject
    private IntakePivotSubsystem intake;
    @Inject
    @Assisted
    private Side side;
    @Inject
    @Assisted
    private double angle;
    @Inject
    @Assisted
    private HoldType type;
    @Inject
    @Assisted
    private PIDType pidType;

    private boolean isZeroingPID;

    public enum Side {
        LEFT, RIGHT
    }

    public enum HoldType {
        HOLD, NO_HOLD
    }

    public enum PIDType {
        DEFAULT, ZEROING
    }

    @PostConstruct
    public void init() {
        isZeroingPID = pidType == PIDType.ZEROING;
        requires(intake);
    }

    @Override
    protected void initializeDelegate() {
        if (type == HoldType.NO_HOLD) {
            presetIntakePivot();
        }
    }

    @Override
    protected void executeDelegate() {
        if (type == HoldType.HOLD) {
            presetIntakePivot();
        }
    }

    private void presetIntakePivot() {
        switch (side) {
            case LEFT:
                intake.presetLeftIntakePivot(angle, isZeroingPID);
                break;
            case RIGHT:
                intake.presetRightIntakePivot(angle, isZeroingPID);
                break;
        }
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
