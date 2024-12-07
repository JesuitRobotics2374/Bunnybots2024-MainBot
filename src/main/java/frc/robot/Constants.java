package frc.robot;

public class Constants {

    // CAN Bus Names

    public static final String CAN_BUS_NAME_CANIVORE = "FastFD";
    public static final String CAN_BUS_NAME_ROBORIO = "rio";
    public static final String CAN_BUS_NAME_DRIVETRAIN = CAN_BUS_NAME_CANIVORE;

    // Contoller IDs

    public static final int CONTROLLER_USB_PORT_DRIVER = 0; // Drivers Controller
    public static final int CONTROLLER_USB_PORT_OPERATOR = 1; // Ordanence operators controller

    // Shuffleboard

    public static final String DRIVER_READOUT_TAB_NAME = "Driver Readout";

    // Drivetrain Subsystem

    public static final double ROBOT_MAX_SPEED = 6.0;
    public static final double ROBOT_MAX_ROTATIONAL_RATE = 1.5 * Math.PI;

    // ARM Subsystem

    public static final int RIGHT_ARM_MOTOR_ID = 6;
    public static final int LEFT_ARM_MOTOR_ID = 7;
    public static final int ARM_ENCODER_ID = 25;
    public static final double FORWARD_SOFT_STOP = 0.15;
    public static final double BACKWARD_SOFT_STOP = -0.265;
    public static final double FEED_FORWARD_VOLTAGE = 0.3;

    public static final double ARM_PID_P = 4.0;
    public static final double ARM_PID_I = 0.25;
    public static final double ARM_PID_D = 0.0;

    // Auto Static Groups

    public static final double ALL_GROUPS_ROTATION = -90;

    public static final int[] A_GROUP_MEMBERS = { 1, 2, 3 };
    public static final double A_GROUP_X = 0;
    public static final double A_GROUP_Y = 0;

    public static final int[] B_GROUP_MEMBERS = { 4, 5, 6 };
    public static final double B_GROUP_X = 0;
    public static final double B_GROUP_Y = 0;

    public static final int[] C_GROUP_MEMBERS = { 7, 8, 9 };
    public static final double C_GROUP_X = 6.95;
    public static final double C_GROUP_Y = 5.50;

    public static final int[] D_GROUP_MEMBERS = { 10, 11, 12 };
    public static final double D_GROUP_X = 0;
    public static final double D_GROUP_Y = 0;

    // Vacuum Subsystem

    public static final int VAC_1_ID = 54;
    public static final int VAC_2_ID = 55;
    public static final int VAC_3_ID = 56;

}