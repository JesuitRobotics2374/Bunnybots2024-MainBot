package frc.robot.commands;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

/**
 * AlignToSpeakerCommand
 */
public class AlignToSpeakerCommand extends Command {

    CommandSwerveDrivetrain subsystem;
    ProfiledPIDController controller;
    SwerveRequest.FieldCentric request = new SwerveRequest.FieldCentric();

    public AlignToSpeakerCommand(CommandSwerveDrivetrain subDrivetrain) {
        System.out.println("ALIGN TO THE TING");
        subsystem = subDrivetrain;
        addRequirements(subDrivetrain);
        controller = new ProfiledPIDController(2, 0, 0.1, new Constraints(Math.PI * 4, Math.PI * 3));
        controller.enableContinuousInput(-Math.PI, Math.PI);
        controller.setTolerance(0.03, 0.05);
    }

    @Override
    public void initialize() {
        boolean flag = false;
        var alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            flag = alliance.get() == Alliance.Red;
        }
        Translation2d offset = (flag ? new Translation2d(16.3, 5.55) : new Translation2d(0.3, 5.55))
                .minus(subsystem.getState().Pose.getTranslation());
        controller.setGoal(offset.getAngle().getRadians());
        System.out.println(Math.toDegrees(controller.getGoal().position));
    }

    @Override
    public void execute() {
        double rate = controller.calculate(subsystem.getState().Pose.getRotation().getRadians());
        if (rate < 0 - controller.getPositionTolerance()) {
            rate = Math.min(rate, Math.abs(controller.getPositionError()) > .1 ? -1.2 : -0.6);
        } else if (rate > 0 + controller.getPositionTolerance()) {
            rate = Math.max(rate, Math.abs(controller.getPositionError()) > .1 ? 1.2 : 0.6);
        }
        System.out.println(rate);
        subsystem.setControl(request.withRotationalRate(rate));
    }

    @Override
    public boolean isFinished() {
        return controller.atGoal();
    }
}