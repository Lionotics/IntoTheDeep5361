package teamcode.hardware;

import android.util.Log;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import teamcode.helpers.PIDController;


@Config
public class HSlides {
    // PID constants kP, kI, and kD
    public static double kP = 0.5;
    public static double kI = 0;
    public static double kD = 0;
    public static int THRESHOLD = 5; // If the slides are within this threshold of the target position, they are considered to be at the target position
    private PIDController controller; // The PID controller for the slides
    public static HSlides.SlideState slideState = HSlides.SlideState.HOLDING; // The current state of the slides
    private DcMotor hSlide;

    // Initializes the hardware
    public void init(@NonNull HardwareMap hwMap) {
        hSlide = hwMap.get(DcMotor.class, "hSlide");
        hSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        controller = new PIDController(kP, kI, kD);
        controller.setTolerance(THRESHOLD);
    }
    private void setPower(double power) {
        hSlide.setPower(-power);
    }

    // A method that gets run each cycle in the opmode
    // It runs the appropriate method based on the liftState
    public void loop() {
        switch (slideState) {
            case AUTO_MOVE:
                pidLoop();
                break;
            case MANUAL_IN:
                slideIn();
                break;
            case MANUAL_OUT:
                Log.d("Teamcode", "loop ran");
                slideOut();
                break;
            case HOLDING:
                horizontalHold();
                break;
        }
    }

    // Sets a target position for the vertical slides to move to
    // Public because we should be able to move the slides to a target position from other classes
    public void moveToPosition(HSlides.SlidePositions position) {
        controller.setSetPoint(position.pos); // Set the target position for the PID controller
        setSlideState(SlideState.AUTO_MOVE);
    }

    // Setters for the liftState
    // These are the methods that the teleop will call to move the slides
    // Notice there is no method to move the slides to AUTO_MOVE state
    // This is because the moveToPosition method should be used to move the slides to a target position which will set the liftState to AUTO_MOVE
    public void manualOut() {
        setSlideState(SlideState.MANUAL_OUT);
        Log.d("Teamcode", "Sate set");
    }

    public void manualIn() {
        setSlideState(SlideState.MANUAL_IN);
    }

    public void hold() {
        if (slideState != SlideState.AUTO_MOVE || controller.atSetPoint()) {
            setSlideState(SlideState.HOLDING);
        }
    }

    // Set the power for the vertical slides
    // Private because this should only be called by loop method
    private void slideIn() {
        setPower(-1);
    }

    private void slideOut() {
        setPower(1);
        Log.d("Teamcode", "Power set");
    }

    // The method that gets run each cycle in the opmode (while the slides are in AUTO_MOVE state)
    private void pidLoop() {
        controller.setPID(kP, kI, kD);
        controller.setTolerance(THRESHOLD);
        double pow = getPidPower();
        setPower(pow);
    }

    // Sets a small power to the slides to counteract gravity
    // Private because this should only be called by loop method
    private void horizontalHold() {
        if (getPos() > SlidePositions.IN.pos - THRESHOLD) {
            setPower(.2);
        } else {
            setPower(-.5);
        }
    }

    public double getTargetPos() {
        return controller.getSetPoint();
    }

    public HSlides.SlideState getSlideState() {
        return slideState;
    }

    // Setter for the liftState
    // Private because no other class should be setting the liftState directly
    private void setSlideState(HSlides.SlideState state) {
        slideState = state;
    }

    public double getPidPower() {
        double position = getPos();
        double pow = controller.calculate(position);
        return pow;
    }

    public double getPos() {
        return -hSlide.getCurrentPosition();
    }

    // Possible states for the vertical slides
    public enum SlideState {
        AUTO_MOVE, MANUAL_IN, MANUAL_OUT, HOLDING
    }

    // Position constants for the slides to move to (in encoder ticks)
    public enum SlidePositions {
        OUT(140), IN(-10);

        public final int pos;

        SlidePositions(int pos) {
            this.pos = pos;
        }
    }
}