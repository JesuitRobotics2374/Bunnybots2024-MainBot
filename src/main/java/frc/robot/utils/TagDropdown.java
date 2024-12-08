package frc.robot.utils;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class TagDropdown {

    private final SendableChooser<Command> commandChooser = new SendableChooser<>();

    private Command t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12;

    public TagDropdown() {

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

    }

    public SendableChooser<Command> getDropdown() {
        return commandChooser;
    }

}
