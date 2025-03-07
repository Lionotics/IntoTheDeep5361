package teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;

@Config
public class Consts {
    // Intake
    public static double I_CLAW_CLOSE = 0.1;
    public static double I_CLAW_OPEN = .6;
    public static double PIVOT_BARRIER = 0.7;
    public static double PIVOT_GRAB = 0.568;
    public static double PIVOT_TRANSFER = 1;

    // Wrist
    public static double W_WEST = 0.6;
    public static double W_NORTHWEST = 0.45;
    public static double W_NORTH = 0.3075;
    public static double W_NORTHEAST = 0.15;
    public static double W_EAST = 0.0;

    // EndEffector
    public static double E_CLAW_CLOSE = 0;
    public static double E_CLAW_OPEN = .2;
    public static double BIG_GRAB = .69;
    public static double BIG_SPECIMEN = 0.73;
    public static double BIG_TRANSFER = 0.723;
    public static double BIG_SAMPLE = 0.35;
    public static double BIG_WALL = 0.11;
    public static double LITTLE_SPECIMEN = 0.8;
    public static double LITTLE_TRANSFER = 0;
    public static double LITTLE_SAMPLE = 1;
    public static double LITTLE_WALL = 0.85;

    // Monitor Thresholds
    public static double BIG_THRESH = 0.05;
}