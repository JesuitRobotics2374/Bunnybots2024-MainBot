package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.auto.BackUntilCanSeeTag;
import frc.robot.commands.auto.DriveAndSeek;
import frc.robot.commands.auto.DriveDynamic;
import frc.robot.commands.auto.DriveDynamicY;
import frc.robot.commands.auto.OriginToStatic;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.VacuumSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class ApproachTagAuto extends InstantCommand {

    public ApproachTagAuto(CommandSwerveDrivetrain drivetrain, VisionSubsystem visionSubsystem, int tag_id,
            VacuumSubsystem vac, ArmSubsystem arm) {

        // DistanceAndAngle d = visionSubsystem.getTagDistanceAndAngle(tag_id);

        Command intake = new InstantCommand(() -> {
            vac.intakeFull();
        });
        Command blind = new BackUntilCanSeeTag(drivetrain, visionSubsystem);
        Command align = new InstantCommand(() -> {
            drivetrain.alignToVision();
        });
        Command staticNav = new OriginToStatic(drivetrain, visionSubsystem, tag_id);
        Command seek = new DriveAndSeek(drivetrain, visionSubsystem, tag_id);
        Command squareUp = new DriveDynamicY(drivetrain, visionSubsystem, tag_id, 1, 0.15);
        Command drive = new DriveDynamic(drivetrain, visionSubsystem, tag_id, 1.4, 1.5);
        Command squareUpClose = new DriveDynamicY(drivetrain, visionSubsystem, tag_id, 0.3, 0.01);
        Command lower = new InstantCommand(() -> {
            arm.setGoal(-0.2 * 360); // Adjust
        });
        Command driveClose = new DriveDynamic(drivetrain, visionSubsystem, tag_id, 0.2, 0.3);
        Command outtake = new InstantCommand(() -> {
            vac.outtake();
        });
        Command timer = new WaitCommand(0.6);
        Command stop = new InstantCommand(() -> {
            vac.stop();
        });

        SequentialCommandGroup approach = new SequentialCommandGroup(intake, blind, align, staticNav, seek,
                squareUp, drive, squareUpClose, lower, driveClose, outtake, timer, stop);

        approach.schedule();

    }
}
