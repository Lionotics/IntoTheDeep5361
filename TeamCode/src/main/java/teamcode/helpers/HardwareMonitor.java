package teamcode.helpers;

import com.qualcomm.robotcore.hardware.Servo;

public class HardwareMonitor {
    double threshold;
    Servo servo;
    public HardwareMonitor(Servo servo, int threshold) {
        this.servo = servo;
        this.threshold = threshold;
    }
    public boolean isWithinThreshold(double pos) {
        return Math.abs(servo.getPosition() - pos) < threshold;
    }
}
