package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    private final RobotContainer m_robotContainer = new RobotContainer();
    public static boolean isRed = false;

    @Override
    public void robotInit() {
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

    }

    @Override
    public void testInit() {
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
