package frc.robot;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import frc.robot.subsystems.*;
import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;
import frc.robot.subsystems.DrivetrainSubsystem.TunerConstants;
import frc.robot.util.AutonomousChooser;

public class RobotContainer {

    private final SendableChooser<Command> commandChooser = new SendableChooser<>();
    private final SendableChooser<Command> autoType = new SendableChooser<>();

    private double MaxSpeed = Constants.ROBOT_MAX_SPEED;
    private double MaxAngularRate = Constants.ROBOT_MAX_ROTATIONAL_RATE;

    private final CommandSwerveDrivetrain m_DrivetrainSubsystem = TunerConstants.DriveTrain;

    private final VacuumMaster m_VacuumMaster;
    private final VacuumSubsystem m_VacuumSubsystem1;
    private final VacuumSubsystem m_VacuumSubsystem2;
    private final VacuumSubsystem m_VacuumSubsystem3;
    private final VisionSubsystem m_VisionSubsystem;
    private final ArmSubsystem m_ArmSubsystem;

    private Command t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12;
    private Command regular, away, wait;

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // I want field-centric
                                                                     // driving in open loop

    private final CommandXboxController m_driveController = new CommandXboxController(
            Constants.CONTROLLER_USB_PORT_DRIVER);
    private final CommandXboxController m_operatorController = new CommandXboxController(
            Constants.CONTROLLER_USB_PORT_OPERATOR);

    private boolean slow = false;
    private boolean roll = false;

    private int SELECTED_AUTO_TAG = -1;

    public RobotContainer() {

        m_ArmSubsystem = new ArmSubsystem();

        // m_VacuumSubsystem = new VacuumSubsystem();

        m_VacuumSubsystem1 = new VacuumSubsystem(Constants.VAC_1_ID);
        m_VacuumSubsystem2 = new VacuumSubsystem(Constants.VAC_2_ID);
        m_VacuumSubsystem3 = new VacuumSubsystem(Constants.VAC_3_ID);
        m_VacuumMaster = new VacuumMaster(m_VacuumSubsystem1, m_VacuumSubsystem2,
                m_VacuumSubsystem3);

        m_VisionSubsystem = new VisionSubsystem();

        // m_ClimberSubsystem = new ClimberSubsystem();
        registerAutoCommands();
        System.out.println("container created");
        configureShuffleBoard();
        resetDrive();
        configureButtonBindings();

        t1 = new InstantCommand(() -> {
        });
        t1.setName("1");
        t2 = new InstantCommand(() -> {
        });
        t2.setName("2");
        t3 = new InstantCommand(() -> {
        });
        t3.setName("3");
        t4 = new InstantCommand(() -> {
        });
        t4.setName("4");
        t5 = new InstantCommand(() -> {
        });
        t5.setName("5");
        t6 = new InstantCommand(() -> {
        });
        t6.setName("6");
        t7 = new InstantCommand(() -> {
        });
        t7.setName("7");
        t8 = new InstantCommand(() -> {
        });
        t8.setName("8");
        t9 = new InstantCommand(() -> {
        });
        t9.setName("9");
        t10 = new InstantCommand(() -> {
        });
        t10.setName("10");
        t11 = new InstantCommand(() -> {
        });
        t11.setName("11");
        t12 = new InstantCommand(() -> {
        });
        t12.setName("12");

        regular = new InstantCommand(() -> {
        });
        regular.setName("regular");
        away = new InstantCommand(() -> {
        });
        away.setName("away");
        wait = new InstantCommand(() -> {
        });
        wait.setName("wait");

        commandChooser.addOption("Tag 1", t1);
        commandChooser.addOption("Tag 2", t2);
        commandChooser.addOption("Tag 3", t3);
        commandChooser.addOption("Tag 4", t4);
        commandChooser.addOption("Tag 5", t5);
        commandChooser.addOption("Tag 6", t6);
        commandChooser.addOption("Tag 7", t7);
        commandChooser.addOption("Tag 8", t8);
        commandChooser.addOption("Tag 9", t9);
        commandChooser.addOption("Tag 10", t10);
        commandChooser.addOption("Tag 11", t11);
        commandChooser.addOption("Tag 12", t12);

        autoType.setDefaultOption("Regular", regular);
        autoType.addOption("Move Away", away);
        autoType.addOption("Wait Before", wait);
    }

    private void setAutoTag(int tag) {
        System.out.println("PICKED TAG FOR AUTO: " + tag);
        this.SELECTED_AUTO_TAG = tag;
    }

    /**
     * Reset the default drive command
     */
    public void resetDrive() {
        m_DrivetrainSubsystem.setDefaultCommand( // Drivetrain will execute this command periodically
                m_DrivetrainSubsystem.applyRequest(
                        () -> drive.withVelocityX(square(deadband(m_driveController.getLeftY(), 0.1)) * MaxSpeed) // Drive
                                // forward
                                // with
                                // negative Y (forward)
                                .withVelocityY(square(deadband(m_driveController.getLeftX(), 0.1)) * MaxSpeed) // Drive
                                                                                                               // left
                                                                                                               // with
                                                                                                               // negative
                                                                                                               // X
                                // (left)
                                .withRotationalRate(
                                        square(clampAdd(deadband(m_driveController.getRightX(), 0.1),
                                                deadband(m_operatorController.getLeftX(), 0.1)) * MaxAngularRate)) // Drive
                // counterclockwise
                // with
                // negative X (left)
                ));
        // Java
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
     * Register Auto Commands
     */
    public void registerAutoCommands() {
        // NamedCommands.registerCommand("Shoot",
        // new AutoShootCommand(m_ManipulatorSubsystem, m_DrivetrainSubsystem,
        // m_ArmSubsystem));
        // NamedCommands.registerCommand("Intake", new
        // IntakeCommand(m_ManipulatorSubsystem));
        NamedCommands.registerCommand("Reset Pose", new InstantCommand(() -> m_DrivetrainSubsystem.alignToVision()));
        NamedCommands.registerCommand("Slam Arm",
                new FunctionalCommand(() -> m_ArmSubsystem.setGoal(Constants.BACKWARD_SOFT_STOP * 360), () -> {
                }, interrupted -> {
                }, () -> m_ArmSubsystem.atGoal()).andThen(new WaitCommand(0.4)).withTimeout(2));
        NamedCommands.registerCommand("Shoot No Aim", new InstantCommand(() -> {
        }));
    }

    /**
     * Set up the Shuffleboard
     */
    public void configureShuffleBoard() {

        ShuffleboardTab tab = Shuffleboard.getTab(Constants.DRIVER_READOUT_TAB_NAME);

        // New List Layout
        ShuffleboardContainer pos = tab.getLayout("Position", "List Layout").withPosition(0, 0).withSize(1, 2);

        // Field
        tab.add(m_DrivetrainSubsystem.getField()).withPosition(2, 1).withSize(5, 3);

        // Modes
        tab.addBoolean("Slow Mode", () -> isSlow()).withPosition(2, 0).withSize(1, 1);
        tab.addBoolean("Roll Mode", () -> isRoll()).withPosition(4, 0).withSize(1, 1);

        // Robot (Reverse order for list layout)
        pos.addDouble("Robot R", () -> m_DrivetrainSubsystem.getState().Pose.getRotation().getDegrees()).withPosition(0,
                2);
        pos.addDouble("Robot Y", () -> m_DrivetrainSubsystem.getState().Pose.getY()).withPosition(0, 1);
        pos.addDouble("Robot X", () -> m_DrivetrainSubsystem.getState().Pose.getX()).withPosition(0, 0);

        tab.addDouble("Speed", () -> m_DrivetrainSubsystem.getRobotOverallVelocity()).withPosition(0, 2).withSize(2, 2)
                .withWidget("Simple Dial");

        // Arm
        tab.addDouble("Arm Goal", () -> m_ArmSubsystem.getController().getSetpoint().position).withPosition(1, 0)
                .withSize(1, 1);
        tab.addDouble("Arm Actual", () -> m_ArmSubsystem.getEncoder().getAbsolutePosition().getValueAsDouble())
                .withPosition(1, 1).withSize(1, 1);

        // Vac
        tab.addString("Active Vacuum", () -> m_VacuumMaster.getTargetVacAsString()).withPosition(7, 0).withSize(1, 1);
        tab.addString("Vac 1 Status", () -> m_VacuumSubsystem1.getState()).withPosition(7, 1).withSize(1, 1);
        tab.addString("Vac 2 Status", () -> m_VacuumSubsystem2.getState()).withPosition(7, 2).withSize(1, 1);
        tab.addString("Vac 3 Status", () -> m_VacuumSubsystem3.getState()).withPosition(7, 3).withSize(1, 1);

        // Auto
        tab.add("Auto Tag", commandChooser).withPosition(3, 0).withSize(1, 1);
        tab.add("Auto Type", autoType).withPosition(5, 0).withSize(2, 1);

    }

    /**
     * Setup all of the button controls for the robot
     */
    public void configureButtonBindings() {

        // DRIVE CONTROLLER

        m_driveController.back().onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.seedFieldRelative()));
        m_driveController.leftBumper().onTrue(new InstantCommand(() -> toggleSlow()));
        m_driveController.rightBumper().onTrue(new InstantCommand(() -> toggleRoll()));

        m_driveController.start().onTrue(m_DrivetrainSubsystem.runOnce(() -> m_DrivetrainSubsystem.alignToVision()));

        m_driveController.povUp().whileTrue(m_ArmSubsystem.runOnce(() -> m_ArmSubsystem.raise()));
        m_driveController.povDown().whileTrue(m_ArmSubsystem.runOnce(() -> m_ArmSubsystem.lower()));

        // m_driveController.a().onTrue(
        // m_VisionSubsystem.runOnce(() -> {
        // System.out.println("started test case");
        // m_VisionSubsystem.doStaticAlign(m_DrivetrainSubsystem,
        // SELECTED_AUTO_TAG);
        // }));

        // m_driveController.b().onTrue(
        // m_VisionSubsystem.runOnce(() -> {
        // System.out.println("started finder case");
        // m_VisionSubsystem.approachDynamically(m_DrivetrainSubsystem,
        // 7,
        // m_VacuumMaster, m_ArmSubsystem);
        // }));

        // m_driveController.y().onTrue(
        // m_VisionSubsystem.runOnce(() -> {
        // System.out.println("SEEKING");
        // m_VisionSubsystem.driveAndSeek(m_DrivetrainSubsystem, 8);
        // }));

        // m_driveController.b().onTrue(
        // m_VisionSubsystem.runOnce(() -> {
        // System.out.println("PAN");
        // m_VisionSubsystem.panDynamically(m_DrivetrainSubsystem, 8);
        // }));
        // m_driveController.a().onTrue(
        // m_VisionSubsystem.runOnce(() -> {
        // System.out.println("DRIVE");
        // m_VisionSubsystem.driveDynamically(m_DrivetrainSubsystem, 8);
        // }));

        m_driveController.start().onTrue(
                m_VisionSubsystem.runOnce(() -> {
                    m_VisionSubsystem.grabMisc(SELECTED_AUTO_TAG);
                }));

        // m_driveController.b().onTrue(
        // m_VisionSubsystem.runOnce(() -> {
        // m_VisionSubsystem.approachTeleop(m_DrivetrainSubsystem,
        // SELECTED_AUTO_TAG, m_VacuumSubsystem2, m_ArmSubsystem);
        // }));

        // OPERATOR CONTROLLER

        // m_operatorController.y().onTrue(m_VacuumSubsystem1.runOnce(() ->
        // m_VacuumSubsystem1.intakeFull()));
        // m_operatorController.x().onTrue(m_VacuumSubsystem1.runOnce(() ->
        // m_VacuumSubsystem1.stop()));

        m_operatorController.y().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.intakeFull()));
        m_operatorController.b().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.intakePartial()));
        m_operatorController.x().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.stop()));
        m_operatorController.a().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.outtake()));

        m_operatorController.povLeft().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.setTargetVac(3)));
        m_operatorController.povUp().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.setTargetVac(2)));
        m_operatorController.povRight().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.setTargetVac(1)));
        // make one that does all 3 pls :) (on down POV) thx
        // gotcha :)
        m_operatorController.povDown().onTrue(m_VacuumMaster.runOnce(() -> m_VacuumMaster.targetAll()));

        m_operatorController.rightBumper().whileTrue(m_ArmSubsystem.runOnce(() -> m_ArmSubsystem.raise()));
        m_operatorController.leftBumper().whileTrue(m_ArmSubsystem.runOnce(() -> m_ArmSubsystem.lower()));

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
            MaxAngularRate = Math.PI * 1;
        } else if (roll) {
            MaxSpeed = 1.5;
            MaxAngularRate = Math.PI * 1; // from 1
        } else {
            MaxSpeed = 3; // from 3
            MaxAngularRate = Math.PI * 1.5;
        }
        System.out.println(MaxSpeed);
    }

    /**
     * Accessor to the DriveTrain Subsystem
     * 
     * @return The DriveTrain Subsystem
     */
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
        System.out.println("ran receiver");
        Command sel = commandChooser.getSelected();
        System.out.println("------- " + sel.getName());
        int tagToNav = -1;
        try {
            tagToNav = Integer.parseInt(sel.getName());
        } catch (Exception e) {
        }
        Command type = autoType.getSelected();
        boolean waitAction = false;
        boolean awayAction = false;
        if (type.getName().equals("away")) {
            awayAction = true;
        } else if (type.getName().equals("wait")) {
            waitAction = true;
        }
        m_VisionSubsystem.approachDynamically(m_DrivetrainSubsystem,
                tagToNav,
                m_VacuumMaster, m_ArmSubsystem, awayAction, waitAction);
    }
}
