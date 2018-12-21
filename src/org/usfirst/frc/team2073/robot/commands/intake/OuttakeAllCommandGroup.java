package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.shooter.OuttakeShooterCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakePivotSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class OuttakeAllCommandGroup extends AbstractLoggingCommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakeSideRollerSubsystem intakeSide;
    private final ShooterSubsystem shooter;


    @Inject
    public OuttakeAllCommandGroup(IntakeSideRollerSubsystem intakeSide
            , ShooterSubsystem shooter, OuttakeShooterCommand outtakeShooter
            , OuttakeSidesCommand outtakeSides, IntakePivotSubsystem intakePivot, OuttakePivotPresetCommand intakePivotOut) {

        this.shooter = shooter;
        this.intakeSide = intakeSide;
        IntakePivotSubsystem intakePivot1 = intakePivot;

        addParallel(intakePivotOut);
        addParallel(outtakeSides);
        addParallel(outtakeShooter);

        setInterruptible(false);
    }

    @Override
    protected void endDelegate() {
        shooter.motorStop();
        intakeSide.motorStop();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }
}
