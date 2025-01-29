package teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.hardware.DriveTrain;
import teamcode.hardware.HSlides;
import teamcode.hardware.VSlides;

public class Robot {
    private static Robot instance = new Robot();
    public DriveTrain driveTrain = new DriveTrain();
    public HSlides hSlides = new HSlides();
    public VSlides vSlides = new VSlides();
    public Transfer transfer = new Transfer();

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
        vSlides.init(hwMap);
        transfer.init(hwMap);
        hSlides.init(hwMap);
    }
}
