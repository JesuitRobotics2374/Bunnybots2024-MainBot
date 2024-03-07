package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Constants;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ManipulatorSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;

public class ShootCommand extends SequentialCommandGroup {

    ManipulatorSubsystem subsystem;
    ArmSubsystem armSubsystem;
    CommandSwerveDrivetrain swerveDrivetrain;
    static ShootCommand instance = null;
    public static boolean isSequenceActive = false;

    public ShootCommand(ManipulatorSubsystem subsystem, CommandSwerveDrivetrain swerveDrivetrain,
            ArmSubsystem armSubsystem) {
        this.subsystem = subsystem;
        this.swerveDrivetrain = swerveDrivetrain;
        this.armSubsystem = armSubsystem;

        /// Commands

        InstantCommand startShooter = new InstantCommand(() -> subsystem.startShooter());

        Command alignDrivetrain = new AlignToSpeakerCommand(swerveDrivetrain); // used in aimCommands
        Command alignArm = new FunctionalCommand(
                () -> armSubsystem.shoot(), () -> {
                }, interrupted -> {
                }, () -> armSubsystem.atGoal()); // used in aimCommands

        Command aimCommands = new WaitCommand(0.2)
                .andThen(new ParallelCommandGroup(alignDrivetrain, alignArm))
                .andThen(new WaitCommand(0.02));

        Command checkShooterSpeed = new WaitUntilCommand(() -> subsystem.shooterAtMaxSpeed()).withTimeout(0.75);

        InstantCommand intakeToShooter = new InstantCommand(() -> subsystem.intake());

        InstantCommand stopIntake = new InstantCommand(() -> subsystem.stopIntake());
        InstantCommand stopShooter = new InstantCommand(() -> subsystem.stopShooter());
        InstantCommand resetArm = new InstantCommand(
                () -> armSubsystem.setGoal(Constants.BACKWARD_SOFT_STOP * 360));
        InstantCommand nullifyInstance = new InstantCommand(() -> instance = null);

        InstantCommand checkAndCancel = new InstantCommand(() -> {
            if (instance == null) {
                instance = this;
            } else {
                subsystem.intake();
                instance.cancel();
                instance = null;
            }
        });

        System.out.println(instance);

        addRequirements(subsystem, swerveDrivetrain, armSubsystem);
        addCommands(startShooter, checkAndCancel,
                new ParallelCommandGroup(aimCommands, checkShooterSpeed),
                intakeToShooter, new WaitCommand(.7),
                new ParallelCommandGroup(resetArm, stopShooter, stopIntake),
                nullifyInstance);
    }
}
