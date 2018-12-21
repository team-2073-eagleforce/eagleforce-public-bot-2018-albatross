package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.shooter.IntakeShooterCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

public class IntakeAllWithEndCommandGroup extends AbstractLoggingCommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakeSideRollerSubsystem intakeSide;
    private final ShooterSubsystem shooter;


    @Inject
    public IntakeAllWithEndCommandGroup(IntakeSideRollerSubsystem intakeSide
            , ShooterSubsystem shooter, IntakeShooterCommand intakeShooter
            , IntakeSidesCommand intakeSides) {

        this.shooter = shooter;
        this.intakeSide = intakeSide;

        requires(shooter);
        requires(intakeSide);

        addParallel(intakeShooter);
        addParallel(intakeSides);
    }

    @Override
    protected void executeDelegate() {

    }

    @Override
    protected void endDelegate() {
        shooter.motorStop();
        intakeSide.motorStop();
    }

    @Override
    protected boolean isFinishedDelegate() {
        return shooter.doesHaveCube();
    }
}
