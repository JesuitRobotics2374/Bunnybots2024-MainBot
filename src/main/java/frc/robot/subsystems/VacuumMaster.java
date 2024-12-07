package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VacuumMaster extends SubsystemBase {

    private VacuumSubsystem vac1;
    private VacuumSubsystem vac2;
    private VacuumSubsystem vac3;

    private VacuumSubsystem targetVac;

    private boolean allVacs;

    public VacuumMaster(VacuumSubsystem vac1, VacuumSubsystem vac2, VacuumSubsystem vac3) {
        this.vac1 = vac1;
        this.vac2 = vac2;
        this.vac3 = vac3;
        targetVac = vac1;
        allVacs = true;
    }

    public void intakeFull() {
        if (allVacs) {
            vac1.intakeFull();
            vac2.intakeFull();
            vac3.intakeFull();
            return;
        }
        targetVac.intakeFull();
    }

    public void intakePartial() {
        if (allVacs) {
            vac1.intakePartial();
            vac2.intakePartial();
            vac3.intakePartial();
            return;
        }
        targetVac.intakePartial();
    }

    public void stop() {
        if (allVacs) {
            vac1.stop();
            vac2.stop();
            vac3.stop();
            return;
        }
        targetVac.stop();
    }

    public void outtake() {
        if (allVacs) {
            vac1.outtake();
            vac2.outtake();
            vac3.outtake();
            return;
        }
        targetVac.outtake();
    }

    public String getTargetVacAsString() {
        if (allVacs) {
            return "ALL";
        } else if (targetVac == vac1) {
            return "Black";
        } else if (targetVac == vac2) {
            return "Green";
        } else if (targetVac == vac3) {
            return "White";
        } else {
            return "ERROR";
        }
    }

    public VacuumSubsystem getTargetVac() {
        return targetVac;
    }

    public VacuumSubsystem[] getVacs() {
        return new VacuumSubsystem[] { vac1, vac2, vac3 };
    }

    public void setTargetVac(int vac) {
        if (vac == 1) {
            targetVac = vac1;
            allVacs = false;
        } else if (vac == 2) {
            targetVac = vac2;
            allVacs = false;
        } else if (vac == 3) {
            targetVac = vac3;
            allVacs = false;
        } else {
            throw new IllegalArgumentException("VacuumMaster.setTargetVac requires an integer between 1 and 3");
        }
    }

    public void targetAll() {
        allVacs = true;
    }

}