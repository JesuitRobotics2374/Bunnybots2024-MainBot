package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    private final RobotContainer m_robotContainer = new RobotContainer();
    public static boolean isRed = false;

    private Command m_autonomousCommand;

    // @SuppressWarnings("unused")

    // TODO make a 1 second periodic to check for the aliance, and if it is present
    // then overwrite the result

    @Override
    public void robotInit() {
        // drivetrain
        enableLiveWindowInTest(false);
        addPeriodic(() -> {
            var ally = DriverStation.getAlliance();
            if (ally.isPresent()) {
                isRed = ally.get() == DriverStation.Alliance.Red;
            }
        }, 0.5);
    }

    public static boolean getIsRed() {
        return isRed;
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void disabledExit() {
        m_robotContainer.getArmSubsystem().resetGoal();
    }

    @Override
    public void teleopInit() {

        m_robotContainer.getDrivetrain().alignWithAliance();
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
        // }
        // m_robotContainer.alignPigeonVision();
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
        // }
    }

    @Override
    public void teleopPeriodic() {
        // if (team == null) {
        // }
    }

    @Override
    public void autonomousInit() {

        m_robotContainer.runAutonomousCommand();

    }

    @Override
    public void autonomousExit() {
        m_robotContainer.getVacuumMaster().stop();
    }
}
