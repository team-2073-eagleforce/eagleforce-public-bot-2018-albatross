package org.usfirst.frc.team2073.robot.subsystems.intake;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Intake;

@Singleton
public class IntakeBottomRollerSubsystem extends Subsystem implements SmartDashboardAware {
    private Victor motor;
    private State state = State.Initializing;

    @Override
    protected void initDefaultCommand() {
    }

    enum State {
        Initializing,
        Intaking,
        Stopped,
        Outaking,
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putString(Intake.NAME + " Bottom State", state.toString());
    }

    @Override
    public void readSmartDashboard() {
    }

    @Inject
    public IntakeBottomRollerSubsystem(@Named("intakeBottomMotor") Victor motor) {
        this.motor = motor;
        state = State.Initializing;
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("Initialized IntakeBottomRollerSubsystem");
    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    public void motorStart() {
        motor.set(1);
        state = State.Intaking;
    }

    public void reverseMotorStart() {
        motor.set(-1);
        state = State.Outaking;
    }

    public void motorStop() {
        motor.set(0);
        state = State.Stopped;
    }
}
