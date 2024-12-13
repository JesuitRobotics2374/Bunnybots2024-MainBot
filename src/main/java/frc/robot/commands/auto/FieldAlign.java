package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Translation2d;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class FieldAlign extends Command {

    private final SwerveRequest.FieldCentric driveRequest = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final CommandSwerveDrivetrain drivetrain;
    private double static_r;

    double currentRotation;

    private boolean doneRotating;

    private static boolean contains(int[] array, int target) {
        for (int num : array) {
            if (num == target) {
                return true;
            }
        }
        return false;
    }

    public FieldAlign(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, int tag_id) {
        this.drivetrain = drivetrain;
        if (contains(Constants.B_GROUP_MEMBERS, tag_id) || contains(Constants.C_GROUP_MEMBERS, tag_id)) { // Blue
            this.static_r = (-1 * Constants.ALL_GROUPS_ROTATION) + 90;
        } else if (contains(Constants.A_GROUP_MEMBERS, tag_id) || contains(Constants.D_GROUP_MEMBERS, tag_id)) { // Red
            this.static_r = (-1 * Constants.ALL_GROUPS_ROTATION) - 90;
        } else {
            throw new IllegalArgumentException("Invalid tag_id: " + tag_id);
        }
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        doneRotating = false;
    }

    @Override
    public void execute() {
        currentRotation = drivetrain.getState().Pose.getRotation().getDegrees();
        if (Math.abs(currentRotation - static_r) < Constants.GENERIC_ROTATION_THRESHOLD) {
            doneRotating = true;
        }
        double rotationalRate = 0;
        if (!doneRotating) {
            double rotationError = static_r - currentRotation;
            double RESign = rotationError / Math.abs(rotationError);
            rotationalRate = rotationError * Constants.ALIGN_ROTATE_SPEED
                    + (RESign * Constants.ALIGN_ROTATIONAL_FEED_FORWARD);
            drivetrain.setControl(
                    driveRequest.withRotationalRate(-rotationalRate));
        }
    }

    @Override
    public boolean isFinished() {
        return doneRotating;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Rotate complete!");
        System.out.println("rm: " + currentRotation);
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}