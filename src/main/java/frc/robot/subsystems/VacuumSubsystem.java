package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class VacuumSubsystem extends SubsystemBase {

    private CANSparkMax vacumm;

    private String state = "Stopped";

    public VacuumSubsystem(int id) {
        vacumm = new CANSparkMax(id, MotorType.kBrushless);
        System.out.println("Created VAC: " + vacumm.getDeviceId());
        this.stop();
    }

    public void intakeFull() {
        vacumm.set(Constants.VACUUM_INTAKE_FULL);
        state = "Intake Max";
    }

    public void intakePartial() {
        vacumm.set(Constants.VACUUM_INTAKE_PARTIAL);

        state = "Intake Partial";

    }

    public void stop() {
        vacumm.set(0.0);
        state = "Stopped";
    }

    public void outtake() {
        vacumm.set(Constants.VACUUM_OUTTAKE);
        state = "Outtake";
    }

    public String getState() {
        return state;
    }
}