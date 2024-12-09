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

    // Arm Subsystem

    public static final double ARM_MOVEMENT_DELTA = 0.01;

    public static final int RIGHT_ARM_MOTOR_ID = 6;
    public static final int LEFT_ARM_MOTOR_ID = 7;
    public static final int ARM_ENCODER_ID = 25;
    public static final double FORWARD_SOFT_STOP = 0.15;
    public static final double BACKWARD_SOFT_STOP = -0.255;
    public static final double FEED_FORWARD_VOLTAGE = 0.3;
    public static final double ENCODER_MAGNETIC_OFFSET = 0.094;

    public static final double ARM_CONTINUOUS_MIN = -0.5;
    public static final double ARM_CONTINUOUS_MAX = 0.5;
    public static final double ARM_POSITION_TOLERANCE = 0.2;
    public static final double ARM_VELOCITY_TOLERANCE = 0.2;
    public static final double ARM_PERIODIC_MIN = -0.3;
    public static final double ARM_PERIODIC_MAX = 0.3;

    public static final double ARM_PID_P = 4.0;
    public static final double ARM_PID_I = 0.25;
    public static final double ARM_PID_D = 0.0;
    public static final double ARM_PID_MAX_VELOCITY = 1.8;
    public static final double ARM_PID_MAX_ACCEL = 1.9;

    public static final double ARM_BASE_OFFSET = 4;
    public static final double ARM_BASE_DELTA = 0.5;

    // Auto Static Groups

    public static final double ALL_GROUPS_ROTATION = -90;

    public static final int[] A_GROUP_MEMBERS = { 1, 2, 3 };
    public static final double A_GROUP_X = 11.365;
    public static final double A_GROUP_Y = 2.031;

    public static final int[] B_GROUP_MEMBERS = { 4, 5, 6 };
    public static final double B_GROUP_X = 4.989;
    public static final double B_GROUP_Y = 2.031;

    public static final int[] C_GROUP_MEMBERS = { 7, 8, 9 };
    public static final double C_GROUP_X = 4.989;
    public static final double C_GROUP_Y = 5.903;

    public static final int[] D_GROUP_MEMBERS = { 10, 11, 12 };
    public static final double D_GROUP_X = 11.365;
    public static final double D_GROUP_Y = 5.903;

    public static final double BLUE_AWAY_X = 1.352;
    public static final double BLUE_AWAY_Y = 4.135;

    public static final double RED_AWAY_X = 14.742;
    public static final double RED_AWAY_Y = 4.135;

    public static final int[] NEARBY_MEMBERS = { 3, 4, 9, 10 };

    // Auto Routine Configs

    public static final double GENERIC_DISTANCE_THRESHOLD = 0.3;
    public static final double GENERIC_ROTATION_THRESHOLD = 1; // Degrees

    public static final double BACKUP_MOVE_SPEED = -1;
    public static final double BACKUP_CENTER_X = 9.0;
    public static final double BACKUP_CENTER_Y = 4.2;

    public static final double SEEK_MOVE_SPEED = 1.6;

    public static final double ALIGN_MOVE_SPEED = 0.75;
    public static final double ALIGN_ROTATE_SPEED = 0.025;
    public static final double ALIGN_ROTATIONAL_FEED_FORWARD = 0.8;

    public static final double AWAY_MOVE_SPEED = 1.75;

    // Vacuum Subsystem

    public static final int VAC_1_ID = 54;
    public static final int VAC_2_ID = 55;
    public static final int VAC_3_ID = 56;

    public static final double VACUUM_INTAKE_FULL = 1;
    public static final double VACUUM_INTAKE_PARTIAL = 0.5;
    public static final double VACUUM_OUTTAKE = -0.5;

    // Vision Subsystem

    public static final int LIMELIGHT_FOV_MMPP = 5000;

}