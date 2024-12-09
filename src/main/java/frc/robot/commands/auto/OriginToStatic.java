package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Translation2d;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class OriginToStatic extends Command {

    private final SwerveRequest.FieldCentric driveRequest = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final CommandSwerveDrivetrain drivetrain;
    private final VisionSubsystem visionSubsystem;
    private final int tag_id;

    private double relativeDistanceMeters;
    private double targetPositionMeters;

    private double static_x;
    private double static_y;
    private double static_r;

    private boolean doneMoving;
    private boolean doneRotating;

    private static boolean contains(int[] array, int target) {
        for (int num : array) {
            if (num == target) {
                return true;
            }
        }
        return false;
    }

    public OriginToStatic(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, int tag_id) {
        this.drivetrain = drivetrain;
        this.visionSubsystem = visionSubsystem;
        this.tag_id = tag_id;
        if (contains(Constants.A_GROUP_MEMBERS, tag_id)) {
            this.static_x = Constants.A_GROUP_X;
            this.static_y = Constants.A_GROUP_Y;
            this.static_r = Constants.ALL_GROUPS_ROTATION;
        } else if (contains(Constants.B_GROUP_MEMBERS, tag_id)) {
            this.static_x = Constants.B_GROUP_X;
            this.static_y = Constants.B_GROUP_Y;
            this.static_r = Constants.ALL_GROUPS_ROTATION;
        } else if (contains(Constants.C_GROUP_MEMBERS, tag_id)) {
            this.static_x = Constants.C_GROUP_X;
            this.static_y = Constants.C_GROUP_Y;
            this.static_r = Constants.ALL_GROUPS_ROTATION + 180.0;
        } else if (contains(Constants.D_GROUP_MEMBERS, tag_id)) {
            this.static_x = Constants.D_GROUP_X;
            this.static_y = Constants.D_GROUP_Y;
            this.static_r = Constants.ALL_GROUPS_ROTATION + 180.0;
        } else {
            throw new IllegalArgumentException("Invalid tag_id: " + tag_id);
        }
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        doneMoving = false;
        doneRotating = false;
    }

    @Override
    public void execute() {
        Translation2d robotPosition = drivetrain.getState().Pose.getTranslation();
        double currentRotation = drivetrain.getState().Pose.getRotation().getDegrees();
        double distanceToTarget = robotPosition.getDistance(new Translation2d(static_x, static_y));
        if (distanceToTarget < Constants.GENERIC_DISTANCE_THRESHOLD) {
            doneMoving = true;
        }
        if (Math.abs(currentRotation - static_r) < Constants.GENERIC_ROTATION_THRESHOLD) {
            doneRotating = true;
        }
        double velocityX = 0;
        double velocityY = 0;
        double rotationalRate = 0;
        if (!doneMoving) {
            double deltaX = static_x - robotPosition.getX();
            double deltaY = static_y - robotPosition.getY();
            double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            velocityX = (deltaX / magnitude) * Constants.ALIGN_MOVE_SPEED;
            velocityY = (deltaY / magnitude) * Constants.ALIGN_MOVE_SPEED;
        }
        if (!doneRotating) {
            double rotationError = static_r - currentRotation;
            double RESign = rotationError / Math.abs(rotationError);
            rotationalRate = rotationError * Constants.ALIGN_ROTATE_SPEED
                    + (RESign * Constants.ALIGN_ROTATIONAL_FEED_FORWARD);
        }
        if (!doneMoving) {
            drivetrain.setControl(
                    driveRequest.withVelocityX(-velocityX).withVelocityY(-velocityY)
                            .withRotationalRate(-rotationalRate));
        } else if (!doneRotating) {
            drivetrain.setControl(
                    driveRequest.withRotationalRate(-rotationalRate));
        }
    }

    @Override
    public boolean isFinished() {
        return doneMoving && doneRotating;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Alignment complete!");
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}