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
import org.usfirst.frc.team2073.robot.util.EagleTimer;

@Singleton
public class IntakeSideRollerSubsystem extends Subsystem implements SmartDashboardAware {
    private Victor leftMotor;
    private Victor rightMotor;
    private State state = State.Initializing;
    private double motorSpeed = .85;
    private EagleTimer timerOn = new EagleTimer();
    private EagleTimer timerOff = new EagleTimer();
    private boolean isFirstRun = true;


    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putString(Intake.NAME + " SideRollers State", state.toString());
        SmartDashboard.setDefaultNumber("IntakeSpeed", motorSpeed);
    }


    @Override
    public void readSmartDashboard() {
        motorSpeed = SmartDashboard.getNumber("IntakeSpeed", motorSpeed);
    }


    @Override
    protected void initDefaultCommand() {
    }

    enum State {
        Initializing,
        Intaking,
        Stopped,
        Outaking,
    }

    @Inject
    public IntakeSideRollerSubsystem(@Named("intakeLeftSideMotor") Victor leftMotor,
                                     @Named("intakeRightSideMotor") Victor rightMotor) {

        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        state = State.Initializing;
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("Initialized IntakeSideRollerSubsystem");

    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    public void motorStart() {
        state = State.Intaking;
        leftMotor.set(motorSpeed);
        rightMotor.set(-motorSpeed);
    }

    public void motorPulse() {
        state = State.Intaking;
        if (timerOn.hasWaited(250) || isFirstRun) {
            isFirstRun = false;
            leftMotor.set(-motorSpeed);
            rightMotor.set(motorSpeed);
            timerOff.startTimer();
            timerOn.reset();
        }
        if (timerOff.hasWaited(250)) {
            leftMotor.set(0);
            rightMotor.set(0);
            timerOn.startTimer();
            timerOff.reset();
        }
    }

    public void resetPulse() {
        isFirstRun = true;
    }

    public void reverseMotorStart() {
        state = State.Outaking;
    }

    public void motorStop() {
        state = State.Stopped;
        leftMotor.set(0);
        rightMotor.set(0);
    }

    public void leftMotorStart() {
        leftMotor.set(-.5);
    }

    public void rightMotorStart() {
        rightMotor.set(-.5);
    }

    public void leftMotorStop() {
        leftMotor.set(0);
    }

    public void rightMotorStop() {
        rightMotor.set(0);
    }

}
