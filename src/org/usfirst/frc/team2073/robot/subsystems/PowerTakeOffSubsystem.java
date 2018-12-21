package org.usfirst.frc.team2073.robot.subsystems;

import com.google.inject.Inject;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2073.robot.util.inject.InjectNamed;

public class PowerTakeOffSubsystem extends Subsystem implements SmartDashboardAware {

    @InjectNamed
    private Solenoid ptoSolenoid;
    @InjectNamed
    private DigitalInput talonTachSensor;

    @Inject
    public void registerSmartDashboardAware(SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
        smartDashboardAwareRegistry.registerInstance(this);
    }

    @Override
    public void updateSmartDashboard() {
        SmartDashboard.putBoolean("RED MEANS STOP (PTO)", isPTOEngaged());
    }

    @Override
    public void readSmartDashboard() {
    }

    @Override
    protected void initDefaultCommand() {
    }

    public boolean isPTOEngaged() {
        return talonTachSensor.get();
    }

    public void engagePTO() {
        ptoSolenoid.set(true);
    }

    public void disengagePTO() {
        ptoSolenoid.set(false);
    }
}
