package teamcode.hardware;

import static com.pedropathing.pathgen.MathFunctions.clamp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


@Config
public class HSlides {
    public static final double MAX_SPEED = 1;
    public static double HOLD_POWER = .1;
    private DcMotor hSlide;

    // Initializes the hardware
    public void init(@NonNull HardwareMap hwMap) {
        hSlide = hwMap.get(DcMotor.class, "hSlide");
        hSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }
    public void setPower(double power) {
        power *= MAX_SPEED;
        power = clamp(power, -MAX_SPEED, MAX_SPEED);
        hSlide.setPower(power);
    }
     public void hold() { hSlide.setPower(HOLD_POWER); }
}
