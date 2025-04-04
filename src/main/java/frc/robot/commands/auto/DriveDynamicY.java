package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class DriveDynamicY extends Command {

    private final CommandSwerveDrivetrain drivetrain;
    private final VisionSubsystem visionSubsystem;
    private final int tag_id;

    private double moveSpeed;
    private double threshold;

    private boolean done;

    public DriveDynamicY(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, int tag_id,
            double speed, double threshold) {
        this.drivetrain = drivetrain;
        this.visionSubsystem = visionSubsystem;
        this.tag_id = tag_id;
        this.moveSpeed = speed;
        this.threshold = threshold;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        done = false;
    }

    @Override
    public void execute() {
        double offsetY = visionSubsystem.getTagPose3d(tag_id).getX();
        double signum = Math.abs(offsetY) / offsetY;
        drivetrain.setControl(
                new SwerveRequest.RobotCentric().withVelocityY(moveSpeed * -signum));
        System.out.println(offsetY);
        if (Math.abs(offsetY) <= threshold || !visionSubsystem.canSeeTag(tag_id)) {
            done = true;
        }
    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Dynamic Y complete!");
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}