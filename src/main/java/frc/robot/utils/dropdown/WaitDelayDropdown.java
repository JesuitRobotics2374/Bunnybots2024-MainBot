package frc.robot.utils.dropdown;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class WaitDelayDropdown {

    private final SendableChooser<Command> commandChooser = new SendableChooser<>();

    private Command t1, t2, t3, t4, t5, t6, t7, t8, t9, t10;

    public WaitDelayDropdown() {

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

        commandChooser.addOption("1 sec", t1);
        commandChooser.addOption("2 sec", t2);
        commandChooser.addOption("3 sec", t3);
        commandChooser.addOption("4 sec", t4);
        commandChooser.addOption("5 sec", t5);
        commandChooser.addOption("6 sec", t6);
        commandChooser.addOption("7 sec", t7);
        commandChooser.addOption("8 sec", t8);
        commandChooser.addOption("9 sec", t9);
        commandChooser.addOption("10 sec", t10);

    }

    public SendableChooser<Command> getDropdown() {
        return commandChooser;
    }

}
