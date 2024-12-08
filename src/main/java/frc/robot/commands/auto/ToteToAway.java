package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class ToteToAway extends Command {

    private final SwerveRequest.FieldCentric driveRequest = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final CommandSwerveDrivetrain drivetrain;

    private double static_x;
    private double static_y;

    private boolean doneMoving;

    private static boolean contains(int[] array, int target) {
        for (int num : array) {
            if (num == target) {
                return true;
            }
        }
        return false;
    }

    public ToteToAway(CommandSwerveDrivetrain drivetrain, int tag_id) {
        this.drivetrain = drivetrain;
        if (contains(Constants.B_GROUP_MEMBERS, tag_id) || contains(Constants.C_GROUP_MEMBERS, tag_id)) { // Blue
            this.static_x = Constants.BLUE_AWAY_X;
            this.static_y = Constants.BLUE_AWAY_Y;
        } else if (contains(Constants.A_GROUP_MEMBERS, tag_id) || contains(Constants.D_GROUP_MEMBERS, tag_id)) { // Red
            this.static_x = Constants.RED_AWAY_X;
            this.static_y = Constants.RED_AWAY_Y;
        } else {
            throw new IllegalArgumentException("Invalid tag_id: " + tag_id);
        }
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        doneMoving = false;
    }

    @Override
    public void execute() {
        Translation2d robotPosition = drivetrain.getState().Pose.getTranslation();
        double distanceToTarget = robotPosition.getDistance(new Translation2d(static_x, static_y));
        if (distanceToTarget < Constants.GENERIC_DISTANCE_THRESHOLD) {
            doneMoving = true;
        }
        double velocityX = 0;
        double velocityY = 0;
        if (!doneMoving) {
            double deltaX = static_x - robotPosition.getX();
            double deltaY = static_y - robotPosition.getY();
            double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            velocityX = (deltaX / magnitude) * Constants.AWAY_MOVE_SPEED;
            velocityY = (deltaY / magnitude) * Constants.AWAY_MOVE_SPEED;
            drivetrain.setControl(
                    driveRequest.withVelocityX(-velocityX).withVelocityY(-velocityY));
        }

    }

    @Override
    public boolean isFinished() {
        return doneMoving;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Move Away Complete!");
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}