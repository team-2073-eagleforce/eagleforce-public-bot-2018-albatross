package org.usfirst.frc.team2073.robot.subsystems.shooter;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import com.team2073.common.speedcontrollers.EagleSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2073.robot.commands.shooter.StopShooterCommand;
import org.usfirst.frc.team2073.robot.conf.AppConstants.Subsystems.Shooter;

@Singleton
public class ShooterSubsystem extends Subsystem implements SmartDashboardAware {

    private final Provider<? extends Command> defaultCommandProvider;
    private final EagleSPX leftShooter;
    private final EagleSPX rightShooter;
    private final DigitalInput shooterBanner;
    private State state = State.Initializing;

    enum State {
        Initializing, Reverse, Forward, Stopped, Holding
    }

    @Inject
    public ShooterSubsystem(Provider<StopShooterCommand> defaultCommandProvider,
                            @Named("leftShooterMotor") EagleSPX leftShooter, @Named("rightShooterMotor") EagleSPX rightShooter,
                            @Named("shooterBanner") DigitalInput shooterBanner) {

        this.defaultCommandProvider = defaultCommandProvider;
        this.leftShooter = leftShooter;
        this.rightShooter = rightShooter;
        this.shooterBanner = shooterBanner;
        state = State.Initializing;
    }

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putString(Shooter.NAME + " State", state.toString());
    }

    @Override
    public void readSmartDashboard() {
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(defaultCommandProvider.get());
    }

    public void motorReverse() {
        state = State.Reverse;
        if (doesHaveCube()) {
            motorHold();
        } else {
            leftShooter.set(ControlMode.PercentOutput, -.5);
            rightShooter.set(ControlMode.PercentOutput, .5);
        }
    }

    public void motorForward() {
        state = State.Forward;
        leftShooter.set(ControlMode.PercentOutput, .325);
        rightShooter.set(ControlMode.PercentOutput, -.325);
    }

    public void lowVoltageShoot() {
        leftShooter.set(ControlMode.PercentOutput, .225);
        rightShooter.set(ControlMode.PercentOutput, -.225);
    }

    public void motorStop() {
        state = State.Stopped;
        leftShooter.set(ControlMode.PercentOutput, 0);
        rightShooter.set(ControlMode.PercentOutput, 0);
    }

    public void motorHold() {
        state = State.Holding;
        leftShooter.set(ControlMode.PercentOutput, -.125);
        rightShooter.set(ControlMode.PercentOutput, .125);
    }

    public boolean doesHaveCube() {
        return shooterBanner.get();
    }

    public void maxShoot() {
        leftShooter.set(ControlMode.PercentOutput, .75);
        rightShooter.set(ControlMode.PercentOutput, -.75);
    }
}
