package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class Robot {
    private static Robot instance = new Robot();
    public DriveTrain driveTrain = new DriveTrain();
    public Slides slides = new Slides();
    public Intake intake = new Intake();
    public EndEffector ee = new EndEffector();

    private Robot() {
        // Private to prevent instantiation
    }

    public static Robot getInstance() {
        // Check if an instance exists
        if (instance == null) {
            // If no instance exists, create one
            instance = new Robot();
        }
        // Return the existing instance
        return instance;
    }

    public void init(HardwareMap hwMap) {
        driveTrain.init(hwMap);
        slides.init(hwMap);
        intake.init(hwMap);
        ee.init(hwMap);
    }
}
