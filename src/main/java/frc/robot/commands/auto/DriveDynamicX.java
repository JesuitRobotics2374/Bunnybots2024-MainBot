package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class DriveDynamicX extends Command {

    private final CommandSwerveDrivetrain drivetrain;
    private final VisionSubsystem visionSubsystem;
    private final int tag_id;

    private boolean done;

    private double speed;
    private double providedDistance;

    public DriveDynamicX(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, int tag_id,
            double speed, double distance) {
        this.drivetrain = drivetrain;
        this.visionSubsystem = visionSubsystem;
        this.tag_id = tag_id;
        this.providedDistance = distance;
        this.speed = speed;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        done = false;
    }

    @Override
    public void execute() {
        if (!visionSubsystem.canSeeTag(tag_id)) {
            done = true;
            return;
        }
        drivetrain.setControl(new SwerveRequest.RobotCentric().withVelocityX(speed));
        double z = visionSubsystem.getTagPose3d(tag_id).getZ();
        double x = visionSubsystem.getTagPose3d(tag_id).getX();
        double distance = Math.sqrt(x * x + z * z);
        System.out.println(distance);
        if (distance <= providedDistance || !visionSubsystem.canSeeTag(tag_id)) {
            done = true;
        }
    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Dynamic X complete!");
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}