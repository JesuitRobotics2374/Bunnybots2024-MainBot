package frc.robot;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;
import frc.robot.subsystems.DrivetrainSubsystem.TunerConstants;
import frc.robot.util.AutonomousChooser;

/**
 * TODO: add rotation control to the Operator Controller
 */

public class RobotContainer {
    private double MaxSpeed = 6; // 6 meters per second desired top speed
    private double MaxAngularRate = 1.5 * Math.PI; // 3/4 of a rotation per second max angular velocity
    private final ChassisSubsystem m_ChassisSubsystem;
    private final CommandSwerveDrivetrain m_DrivetrainSubsystem = TunerConstants.DriveTrain;
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.05).withRotationalDeadband(MaxAngularRate * 0.07) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // I want field-centric
                                                                     // driving in open loop
    private final AutonomousChooser autonomousChooser = new AutonomousChooser();

    private final CommandXboxController m_driveController = new CommandXboxController(
            Constants.CONTROLLER_USB_PORT_DRIVER);

    private boolean slow = false;
    private boolean roll = false;

    /**
     * The robot container. Need I say more?
     */
    public RobotContainer() {
        m_ChassisSubsystem = new ChassisSubsystem();

        System.out.println("container created");
        configureShuffleBoard();
        resetDrive();
        configureButtonBindings();
        // m_DrivetrainSubsystem.getState().Pose = new
        // Pose2d(m_DrivetrainSubsystem.getState().Pose.getTranslation(),
        // new Rotation2d());
        m_DrivetrainSubsystem.seedFieldRelative(new Pose2d());
        // m_DrivetrainSubsystem.getPigeon2().getConfigurator()
        // .apply(new Pigeon2Configuration().withMountPose(new
        // MountPoseConfigs().withMountPoseYaw(0)));

    }

    /**
     * Reset the default drive command
     */
    public void resetDrive() {
        m_DrivetrainSubsystem.setDefaultCommand( // Drivetrain will execute this command periodically
                m_DrivetrainSubsystem.applyRequest(() -> drive.withVelocityX(-m_driveController.getLeftY() * MaxSpeed) // Drive
                        // forward
                        // with
                        // negative Y (forward)
                        .withVelocityY(-m_driveController.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                        .withRotationalRate(-m_driveController.getRightX() * MaxAngularRate) // Drive counterclockwise
                                                                                             // with
                // negative X (left)
                ));
        m_DrivetrainSubsystem.seedFieldRelative();
        if (Utils.isSimulation()) {
            m_DrivetrainSubsystem.seedFieldRelative(new Pose2d(new Translation2d(), Rotation2d.fromDegrees(90)));
        }
    }

    /**
     * Get the main controller
     * 
     * @return The main controller
     */
    public CommandXboxController getMainController() {
        return m_driveController;
    }

    /**
     * Set up the Shuffleboard
     */
    public void configureShuffleBoard() {
        ShuffleboardTab tab = Shuffleboard.getTab(Constants.DRIVER_READOUT_TAB_NAME);
        // tab.add("setPointUp", new InstantCommand(() -> m_ArmSubsystem.setpointUP()));
        // tab.add("setPointBack", new InstantCommand(() ->
        // m_ArmSubsystem.setpointBACK()));
        // tab.add("setPointForward", new InstantCommand(() ->
        // m_ArmSubsystem.setpointFORWARD()));
        // tab.add("setPointDown", new InstantCommand(() ->
        // m_ArmSubsystem.setpointDOWN()));
        if (!m_ChassisSubsystem.isTestRobot()) {
            tab.add(CameraServer.startAutomaticCapture("Camera", 0)).withSize(3, 3).withPosition(6, 0);
        }
        tab.add("Autonomous Mode", getAutonomousChooser().getModeChooser()).withSize(2, 1).withPosition(1, 0);
        // tab.add(m_drivetrainSubsystem.getField()).withSize(3, 2).withPosition(0, 1);
        tab.addBoolean("SLOW", () -> isSlow()).withPosition(1, 1);
        tab.addBoolean("ROLL", () -> isRoll()).withPosition(2, 1);
        // tab.addBoolean("Auto", () ->
        // m_drivetrainSubsystem.getFollower().getCurrentTrajectory().isPresent());
        tab.addDouble("X", () -> m_DrivetrainSubsystem.getState().Pose.getX());
        tab.addDouble("Y", () -> m_DrivetrainSubsystem.getState().Pose.getY());
        tab.addDouble("R", () -> m_DrivetrainSubsystem.getState().Pose.getRotation().getDegrees());
    }

    /**
     * Setup all of the button controls for the robot
     */
    public void configureButtonBindings() {
        // Drivetrain
        m_driveController.back().onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.seedFieldRelative()));
        m_driveController.leftBumper().onTrue(new InstantCommand(() -> toggleSlow()));
        m_driveController.rightBumper().onTrue(new InstantCommand(() -> toggleRoll()));
        // m_driveController.start()
        // .onTrue(new InstantCommand(() ->
        // System.out.println(m_DrivetrainSubsystem.getState().Pose)));
        m_driveController.start().onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.alignToVision()));
    }

    /**
     * Adjusts the input to remove the tolerance while retaining a smooth line with
     * tolerance as 0 and 100 as 100
     * 
     * @param value     The value to adjust
     * @param tolerance The amount of inner area to remove
     * @return The adjusted value
     */
    public static double deadband(double value, double tolerance) {
        if (Math.abs(value) < tolerance)
            return 0.0;

        return Math.copySign(value, (value - tolerance) / (1.0 - tolerance));
    }

    /**
     * Copy sign square
     * 
     * @param value Value to square
     * @return The copy sign square
     */
    public static double square(double value) {
        return Math.copySign(value * value, value);
    }

    public boolean isSlow() {
        return slow;
    }

    public boolean isRoll() {
        return roll;
    }

    public void toggleSlow() {
        slow = !slow;
        updateSpeeds();
    }

    public void toggleRoll() {
        roll = !roll;
        updateSpeeds();
    }

    private void updateSpeeds() {
        if (slow) {
            MaxSpeed = 0.75;
            MaxAngularRate = Math.PI * .5;
        } else if (roll) {
            MaxSpeed = 1.5;
            MaxAngularRate = Math.PI * 1;
        } else {
            MaxSpeed = 3;
            MaxAngularRate = Math.PI * 1.5;
        }
        System.out.println(MaxSpeed);
    }

    /**
     * Accessor to the Autonomous Chooser
     * 
     * @return The Autonomous Chooser
     */
    public AutonomousChooser getAutonomousChooser() {
        return autonomousChooser;
    }

    /**
     * Accessor to the Chassis Subsystem
     * 
     * @return The Chassis Subsystem
     */
    public ChassisSubsystem getChassisSubsystem() {
        return m_ChassisSubsystem;
    }

    /**
     * Accessor to the DriveTrain Subsystem
     * 
     * @return The DriveTrain Subsystem
     */
    public CommandSwerveDrivetrain getDrivetrain() {
        return m_DrivetrainSubsystem;
    }
}
