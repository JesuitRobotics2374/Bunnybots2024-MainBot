package frc.robot.commands.auto;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class BackUntilCanSeeTag extends Command {

    private final CommandSwerveDrivetrain drivetrain;

    private boolean done;

    public BackUntilCanSeeTag(CommandSwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        done = false;
    }

    @Override
    public void execute() {
        drivetrain.setControl(
                new SwerveRequest.RobotCentric().withVelocityX(Constants.BACKUP_MOVE_SPEED));
        double visionX = drivetrain.getField().getObject("Vision").getPose().getTranslation().getX();
        double visionY = drivetrain.getField().getObject("Vision").getPose().getTranslation().getY();
        if (!(Math.round(visionX * 10.0) / 10.0 == Constants.BACKUP_CENTER_X
                && Math.round(visionY * 10.0) / 10.0 == Constants.BACKUP_CENTER_Y)) {
            done = true;
        }

    }

    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Move Back complete!");
        drivetrain.setControl(new SwerveRequest.SwerveDriveBrake());
    }

}