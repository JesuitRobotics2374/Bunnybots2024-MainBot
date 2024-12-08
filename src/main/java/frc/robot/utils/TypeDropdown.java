package frc.robot.utils;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class TypeDropdown {

    private final SendableChooser<Command> commandChooser = new SendableChooser<>();

    private Command regular, away, wait;

    public TypeDropdown() {

        regular = new InstantCommand(() -> {
        });
        regular.setName("regular");
        away = new InstantCommand(() -> {
        });
        away.setName("away");
        wait = new InstantCommand(() -> {
        });
        wait.setName("wait");

        commandChooser.setDefaultOption("Regular", regular);
        commandChooser.addOption("Move Away", away);
        commandChooser.addOption("Wait Before", wait);

    }

    public SendableChooser<Command> getDropdown() {
        return commandChooser;
    }

}
