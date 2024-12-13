package frc.robot;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.ApproachTagAuto;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;
import frc.robot.subsystems.DrivetrainSubsystem.TunerConstants;
import frc.robot.utils.dropdown.MoveDelayDropdown;
import frc.robot.utils.dropdown.TagDropdown;
import frc.robot.utils.dropdown.TypeDropdown;
import frc.robot.utils.dropdown.WaitDelayDropdown;

public class RobotContainer {

    private final TagDropdown autoTag = new TagDropdown();
    private final TypeDropdown autoType = new TypeDropdown();
    private final WaitDelayDropdown autoWait = new WaitDelayDropdown();
    private final MoveDelayDropdown autoMove = new MoveDelayDropdown();

    private double MaxSpeed = Constants.ROBOT_MAX_SPEED;
    private double MaxAngularRate = Constants.ROBOT_MAX_ROTATIONAL_RATE;

    private final CommandSwerveDrivetrain m_DrivetrainSubsystem = TunerConstants.DriveTrain;

    private final VacuumMaster m_VacuumMaster;
    private final VacuumSubsystem m_VacuumSubsystem1;
    private final VacuumSubsystem m_VacuumSubsystem2;
    private final VacuumSubsystem m_VacuumSubsystem3;
    private final VisionSubsystem m_VisionSubsystem;
    private final ArmSubsystem m_ArmSubsystem;

    private ApproachTagAuto autoCommand;

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final CommandXboxController m_driveController = new CommandXboxController(
            Constants.CONTROLLER_USB_PORT_DRIVER);
    private final CommandXboxController m_operatorController = new CommandXboxController(
            Constants.CONTROLLER_USB_PORT_OPERATOR);

    private boolean slow = false;
    private boolean roll = false;

    private double dummyDouble() {
        return 0;
    }

    public RobotContainer() {

        m_ArmSubsystem = new ArmSubsystem();

        m_VacuumSubsystem1 = new VacuumSubsystem(Constants.VAC_1_ID);
        m_VacuumSubsystem2 = new VacuumSubsystem(Constants.VAC_2_ID);
        m_VacuumSubsystem3 = new VacuumSubsystem(Constants.VAC_3_ID);
        m_VacuumMaster = new VacuumMaster(m_VacuumSubsystem1, m_VacuumSubsystem2,
                m_VacuumSubsystem3);

        m_VisionSubsystem = new VisionSubsystem();

        configureShuffleBoard();
        resetDrive();
        configureButtonBindings();

    }

    /**
     * Reset the default drive command
     */
    public void resetDrive() {
        m_DrivetrainSubsystem.setDefaultCommand(
                m_DrivetrainSubsystem.applyRequest(
                        () -> drive.withVelocityX(square(deadband(m_driveController.getLeftY(), 0.1)) * MaxSpeed)
                                .withVelocityY(square(deadband(m_driveController.getLeftX(), 0.1)) * MaxSpeed)
                                .withRotationalRate(
                                        square(clampAdd(deadband(m_driveController.getRightX(), 0.1),
                                                deadband(m_operatorController.getLeftX(), 0.1)) * MaxAngularRate))));
        m_DrivetrainSubsystem.seedFieldRelative();
        if (Utils.isSimulation()) {
            m_DrivetrainSubsystem.seedFieldRelative(new Pose2d(new Translation2d(), Rotation2d.fromDegrees(90)));
        } else {
            if (m_DrivetrainSubsystem.getState().Pose == null) {
                m_DrivetrainSubsystem.getState().Pose = new Pose2d(new Translation2d(), new Rotation2d());
            }
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

        // Limelight
        HttpCamera httpCamera = new HttpCamera("Limelight", "http://limelight.local:5800");
        CameraServer.addCamera(httpCamera);
        tab.add(httpCamera).withPosition(7, 0).withSize(3, 2);

        // New List Layout
        ShuffleboardContainer pos = tab.getLayout("Position", "List Layout").withPosition(0, 0).withSize(2, 3);

        // Field
        tab.add(m_DrivetrainSubsystem.getField()).withPosition(2, 1).withSize(5, 3);

        // Modes
        tab.addBoolean("Slow Mode", () -> isSlow()).withPosition(2, 0).withSize(2, 1);
        tab.addBoolean("Roll Mode", () -> isRoll()).withPosition(5, 0).withSize(1, 1);

        // Robot (Reverse order for list layout)
        pos.addDouble("Robot R", () -> m_DrivetrainSubsystem.getState().Pose.getRotation().getDegrees())
                .withWidget("Gyro");
        ;
        pos.addDouble("Robot Y", () -> m_DrivetrainSubsystem.getState().Pose.getY());
        pos.addDouble("Robot X", () -> m_DrivetrainSubsystem.getState().Pose.getX());

        tab.addDouble("Speed", () -> m_DrivetrainSubsystem.getRobotOverallVelocity()).withPosition(4,
                0).withSize(1, 1)
                .withWidget("Simple Dial");

        // Arm
        // tab.addDouble("Arm Goal", () ->
        // m_ArmSubsystem.getController().getSetpoint().position).withPosition(0, 3)
        // .withSize(1, 1);
        // tab.addDouble("Arm Actual", () ->
        // m_ArmSubsystem.getEncoder().getAbsolutePosition().getValueAsDouble())
        // .withPosition(1, 3).withSize(1, 1);

        // Vac
        tab.addString("Active Vacuum", () -> m_VacuumMaster.getTargetVacAsString()).withPosition(6, 0).withSize(1, 1);
        tab.addString("Green Status", () -> m_VacuumSubsystem1.getState()).withPosition(7, 2).withSize(1, 1);
        tab.addString("White Status", () -> m_VacuumSubsystem2.getState()).withPosition(8, 2).withSize(1, 1);
        tab.addString("Black Status", () -> m_VacuumSubsystem3.getState()).withPosition(9, 2).withSize(1, 1);

        // Auto
        tab.add("Auto Tag", autoTag.getDropdown()).withPosition(7, 3).withSize(2, 1);
        tab.add("Auto Type", autoType.getDropdown()).withPosition(9, 3).withSize(1, 1);
        tab.add("Delay", autoWait.getDropdown()).withPosition(0, 3).withSize(1, 1);
        tab.add("Dwell", autoMove.getDropdown()).withPosition(1, 3).withSize(1, 1);

    }

    /**
     * Setup all of the button controls for the robot
     */
    public void configureButtonBindings() {

        // Driver Controller

        m_driveController.back().onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.seedFieldRelative()));
        m_driveController.start().onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.alignToVision()));

        m_driveController.leftBumper().onTrue(new InstantCommand(() -> toggleSlow()));
        m_driveController.rightBumper().onTrue(new InstantCommand(() -> toggleRoll()));

        // Operator Controller

        m_operatorController.y().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.intakeFull()));
        m_operatorController.b().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.intakePartial()));
        m_operatorController.x().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.stop()));
        m_operatorController.a().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.outtake()));

        m_operatorController.povLeft().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.setTargetVac(3)));
        m_operatorController.povUp().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.setTargetVac(2)));
        m_operatorController.povRight().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.setTargetVac(1)));
        m_operatorController.povDown().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.targetAll()));

        m_operatorController.rightBumper().whileTrue(m_ArmSubsystem.runOnce(() -> m_ArmSubsystem.raise()));
        m_operatorController.leftBumper().whileTrue(m_ArmSubsystem.runOnce(() -> m_ArmSubsystem.lower()));

    }

    public static double deadband(double value, double tolerance) {
        if (Math.abs(value) < tolerance)
            return 0.0;

        return Math.copySign(value, (value - tolerance) / (1.0 - tolerance));
    }

    public static double square(double value) {
        return Math.copySign(value * value, value);
    }

    public static double clampAdd(double value1, double value2) {
        return Math.max(Math.min(value1 + value2, 1), -1);
    }

    public boolean isSlow() {
        return slow;
    }

    public boolean isRoll() {
        return roll;
    }

    public void toggleSlow() {
        slow = !slow;
        if (slow && roll) {
            roll = false;
        }
        updateSpeeds();
    }

    public void toggleRoll() {
        roll = !roll;
        if (slow && roll) {
            slow = false;
        }
        updateSpeeds();
    }

    private void updateSpeeds() {
        if (slow) {
            MaxSpeed = 1;
            MaxAngularRate = Math.PI * 0.75;
        } else if (roll) {
            MaxSpeed = 1.5;
            MaxAngularRate = Math.PI * 1;
        } else {
            MaxSpeed = 3;
            MaxAngularRate = Math.PI * 1.5;
        }
    }

    public CommandSwerveDrivetrain getDrivetrain() {
        return m_DrivetrainSubsystem;
    }

    public ArmSubsystem getArmSubsystem() {
        return m_ArmSubsystem;
    }

    public VacuumMaster getVacuumMaster() {
        return m_VacuumMaster;
    }

    public void alignPigeonVision() {
        m_DrivetrainSubsystem.alignToVision();
    }

    public void runAutonomousCommand() {

        Command sel = autoTag.getDropdown().getSelected();
        int tagToNav = -1;
        try {
            tagToNav = Integer.parseInt(sel.getName());
        } catch (Exception e) {
        }

        Command waitTime = autoWait.getDropdown().getSelected();
        int waitTimeInt = -1;
        try {
            waitTimeInt = Integer.parseInt(waitTime.getName());
        } catch (Exception e) {
        }

        Command moveTime = autoMove.getDropdown().getSelected();
        int moveTimeInt = -1;
        try {
            moveTimeInt = Integer.parseInt(moveTime.getName());
        } catch (Exception e) {
        }

        Command type = autoType.getDropdown().getSelected();
        boolean waitAction = false;
        boolean awayAction = false;

        if (type.getName().equals("away")) {
            awayAction = true;
        } else if (type.getName().equals("wait")) {
            waitAction = true;
        } else if (type.getName().equals("both")) {
            awayAction = true;
            waitAction = true;
        }

        autoCommand = m_VisionSubsystem.approachDynamically(m_DrivetrainSubsystem,
                tagToNav,
                m_VacuumMaster, m_ArmSubsystem, awayAction, waitAction, waitTimeInt, moveTimeInt);

    }
}
