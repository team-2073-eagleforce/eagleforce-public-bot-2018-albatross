package org.usfirst.frc.team2073.robot.commands.intake;

import com.google.inject.Inject;
import com.team2073.common.command.AbstractLoggingCommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.commands.shooter.IntakeShooterCommand;
import org.usfirst.frc.team2073.robot.subsystems.intake.IntakeSideRollerSubsystem;
import org.usfirst.frc.team2073.robot.subsystems.shooter.ShooterSubsystem;

import javax.annotation.PostConstruct;

public class IntakeAllCommandGroup extends AbstractLoggingCommandGroup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntakeSideRollerSubsystem intakeSide;
    private final ShooterSubsystem shooter;
    @Inject
    private IntakeShooterCommand intakeShooter;
    @Inject
    private IntakeSidesCommand intakePulseSides;

    @Inject
    public IntakeAllCommandGroup(IntakeSideRollerSubsystem intakeSide
            , ShooterSubsystem shooter) {

        this.shooter = shooter;
        this.intakeSide = intakeSide;

        requires(shooter);
        requires(intakeSide);

    }

    @PostConstruct
    public void init() {
        addParallel(intakeShooter);
        addParallel(intakePulseSides);
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
        return false;
    }
}
