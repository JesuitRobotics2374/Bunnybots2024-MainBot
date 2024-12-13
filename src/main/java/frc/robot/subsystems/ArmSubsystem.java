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
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ArmSubsystem extends SubsystemBase {
    ProfiledPIDController armController = new ProfiledPIDController(Constants.ARM_PID_P, Constants.ARM_PID_I,
            Constants.ARM_PID_D, new Constraints(Constants.ARM_PID_MAX_VELOCITY, Constants.ARM_PID_MAX_ACCEL));
    TalonFX leftMotor = new TalonFX(Constants.LEFT_ARM_MOTOR_ID);
    TalonFX rightMotor = new TalonFX(Constants.RIGHT_ARM_MOTOR_ID);
    CANcoder encoder = new CANcoder(Constants.ARM_ENCODER_ID, Constants.CAN_BUS_NAME_CANIVORE);
    double goal;
    double speed;
    double manualOffset = Constants.ARM_BASE_OFFSET;

    static ArmSubsystem instance;

    public ArmSubsystem() {
        instance = this;
        leftMotor.setNeutralMode(NeutralModeValue.Brake);
        rightMotor.setNeutralMode(NeutralModeValue.Brake);
        leftMotor.setInverted(false);
        rightMotor.setControl(new Follower(Constants.LEFT_ARM_MOTOR_ID, true));
        encoder.getConfigurator()
                .apply(new MagnetSensorConfigs().withAbsoluteSensorRange(AbsoluteSensorRangeValue.Signed_PlusMinusHalf)
                        .withSensorDirection(SensorDirectionValue.CounterClockwise_Positive)
                        .withMagnetOffset(Constants.ENCODER_MAGNETIC_OFFSET));
        armController.enableContinuousInput(Constants.ARM_CONTINUOUS_MIN, Constants.ARM_CONTINUOUS_MAX);
        armController.setTolerance(Constants.ARM_POSITION_TOLERANCE, Constants.ARM_VELOCITY_TOLERANCE);
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
                armController.calculate(encoder.getAbsolutePosition().getValueAsDouble()), Constants.ARM_PERIODIC_MIN),
                Constants.ARM_PERIODIC_MAX);

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
            goal += 0.02;
            armController.setGoal(goal);
        }

    }

    public void raise() {
        if (goal > Constants.BACKWARD_SOFT_STOP) {
            goal -= 0.02;
            armController.setGoal(goal);
        }
    }

    public void resetGoal() {
        goal = encoder.getAbsolutePosition().getValueAsDouble();
        armController.reset(goal);
        armController.setGoal(goal);
    }

    public void increaseOffset() {
        manualOffset -= Constants.ARM_BASE_DELTA;
    }

    public void decreaseOffset() {
        manualOffset += Constants.ARM_BASE_DELTA;
    }

    public static ArmSubsystem getInstance() {
        return instance;
    }
}
