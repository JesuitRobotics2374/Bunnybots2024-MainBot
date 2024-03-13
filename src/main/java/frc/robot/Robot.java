package frc.robot;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.ADXL345_I2C.AllAxes;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    private final RobotContainer m_robotContainer = new RobotContainer();
    public static Alliance team = null;

    private Command m_autonomousCommand;

    // @SuppressWarnings("unused")

    // TODO make a 1 second periodic to check for the aliance, and if it is present
    // then overwrite the result

    @Override
    public void robotInit() {
        // drivetrain
        SmartDashboard.putNumber("Apriltag Number", 1);
        enableLiveWindowInTest(true);
        addPeriodic(() -> {
            var ally = DriverStation.getAlliance();
            if (ally.isPresent()) {
                team = ally.get();
            }
        }, 0.5);
    }

    public static Alliance getAlliance() {
        return team;
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        // lights
        if (!m_robotContainer.getChassisSubsystem().isTestRobot()) {
        }
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void disabledExit() {
        if (!m_robotContainer.getChassisSubsystem().isTestRobot()) {
        }
        m_robotContainer.getArmSubsystem().resetGoal();
    }

    @Override
    public void teleopInit() {
        
        m_robotContainer.getDrivetrain().alignWithAliance();
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
        // if (team == null) {
        if (DriverStation.getAlliance().isPresent()) {
            team = DriverStation.getAlliance().get();
        }
        // }
        m_robotContainer.alignPigeonVision();
        // m_DrivetrainSubsystem.alignToVision()
    }

    @Override
    public void testInit() {
        // new InstantCommand(robotContainer.getShooter()::disableFlywheel);
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void autonomousPeriodic() {
        // if (team == null) {
        if (DriverStation.getAlliance().isPresent()) {
            team = DriverStation.getAlliance().get();
        }
        // }
    }

    @Override
    public void teleopPeriodic() {
        // if (team == null) {
        if (DriverStation.getAlliance().isPresent()) {
            team = DriverStation.getAlliance().get();
        }
        // }
    }

    @Override
    public void autonomousInit() {
        // if (team == null) {
        if (DriverStation.getAlliance().isPresent()) {
            team = DriverStation.getAlliance().get();
        }
        // }
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();
        m_robotContainer.alignPigeonVision();
        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousExit() {

    }
}
