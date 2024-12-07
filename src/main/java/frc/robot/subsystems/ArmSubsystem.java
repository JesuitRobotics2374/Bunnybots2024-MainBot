package frc.robot.subsystems;

import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class ArmSubsystem extends SubsystemBase {
    ProfiledPIDController armController = new ProfiledPIDController(Constants.ARM_PID_P, Constants.ARM_PID_I,
            Constants.ARM_PID_D, new Constraints(1.8, 1.9));
    TalonFX leftMotor = new TalonFX(Constants.LEFT_ARM_MOTOR_ID);
    TalonFX rightMotor = new TalonFX(Constants.RIGHT_ARM_MOTOR_ID);
    CANcoder encoder = new CANcoder(Constants.ARM_ENCODER_ID, Constants.CAN_BUS_NAME_CANIVORE);
    double goal;
    double speed;
    double manualOffset = 4;

    static ArmSubsystem instance;

    public ArmSubsystem() {
        instance = this;
        leftMotor.setNeutralMode(NeutralModeValue.Brake);
        rightMotor.setNeutralMode(NeutralModeValue.Brake);
        leftMotor.setInverted(false);
        rightMotor.setControl(new Follower(Constants.LEFT_ARM_MOTOR_ID, true));
        encoder.getConfigurator()
                .apply(new MagnetSensorConfigs().withAbsoluteSensorRange(AbsoluteSensorRangeValue.Signed_PlusMinusHalf)
                        .withSensorDirection(SensorDirectionValue.CounterClockwise_Positive).withMagnetOffset(.094));
        armController.enableContinuousInput(-.5, .5);
        armController.setTolerance(0.02, 0.02);
        goal = encoder.getAbsolutePosition().getValueAsDouble();
        armController.setGoal(goal);
    }

    public ProfiledPIDController getController() {
        return armController;
    }

    public CANcoder getEncoder() {
        return encoder;
    }

    @Override
    public void periodic() {
        speed = Math.min(Math.max(
                armController.calculate(encoder.getAbsolutePosition().getValueAsDouble()), -.30), .30);

        // the 12 represents 12 volts
        leftMotor.setVoltage(speed * 12 + Constants.FEED_FORWARD_VOLTAGE
                * Math.sin(encoder.getAbsolutePosition().getValueAsDouble() * Math.PI * -2));
    }

    public void setGoal(double degrees) {
        armController.setGoal(degrees / 360);
        goal = degrees / 360;
    }

    public boolean atGoal() {
        return armController.atGoal();
    }

    // Flipped lower and raise since vacs on other side

    public void lower() {
        if (goal < Constants.FORWARD_SOFT_STOP) {
            goal += 0.01;
            armController.setGoal(goal);
        }

    }

    public void raise() {
        if (goal > Constants.BACKWARD_SOFT_STOP) {
            goal -= .01;
            armController.setGoal(goal);
        }
    }

    public void resetGoal() {
        goal = encoder.getAbsolutePosition().getValueAsDouble();
        armController.reset(goal);
        armController.setGoal(goal);
    }

    public void increaseOffset() {
        manualOffset -= 0.5;
    }

    public void decreaseOffset() {
        manualOffset += 0.5;
    }

    public static ArmSubsystem getInstance() {
        return instance;
    }
}
