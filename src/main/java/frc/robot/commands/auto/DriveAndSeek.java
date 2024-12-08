package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;
import frc.robot.Constants;

public class DriveAndSeek extends Command {

    private static boolean contains(int[] array, int target) {
        for (int num : array) {
            if (num == target) {
                return true;
            }
        }
        return false;
    }

    private final CommandSwerveDrivetrain drivetrain;
    private final VisionSubsystem visionSubsystem;
    private final int tag_id;

    private boolean done;

    private double quadrantSign = 1;

    public DriveAndSeek(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, int tag_id) {
        this.drivetrain = drivetrain;
        this.visionSubsystem = visionSubsystem;
        this.tag_id = tag_id;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        done = false;
        if (contains(Constants.B_GROUP_MEMBERS, tag_id) || contains(Constants.D_GROUP_MEMBERS, tag_id)) {
            quadrantSign *= -1;
        }
        if (contains(Constants.NEARBY_MEMBERS, tag_id)) {
            quadrantSign *= -1;
        }
    }

    @Override
    public void execute() {
        double th = visionSubsystem.getTagDistanceAndAngle(tag_id).getTheta();
        drivetrain.setControl(
                new SwerveRequest.RobotCentric().withVelocityY(Constants.SEEK_MOVE_SPEED * quadrantSign));
        System.out.println(th);
        if (visionSubsystem.canSeeTag(tag_id)) {
            done = true;
        }
    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Seek complete!");
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}