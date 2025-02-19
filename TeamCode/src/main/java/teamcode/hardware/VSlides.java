package teamcode.hardware;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.utility.InstantCommand;
import com.rowanmcalpin.nextftc.core.control.coefficients.PIDCoefficients;
import com.rowanmcalpin.nextftc.core.control.controllers.PIDFController;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorGroup;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition;

@Config
public class VSlides extends Subsystem {
    public static final VSlides INSTANCE = new VSlides();
    public static final double MAX_SLIDE_HEIGHT = 2225;
    public static int THRESHOLD = 20; // If the slides are within this threshold of the target position, they are considered to be at the target position
    public static double TOP_BUCKET = 950;
    public static double BOTTOM_BUCKET = 230;
    public static double TOP_CHAMBER = 515;
    public static double BOTTOM_CHAMBER = 0;
    public static double BOTTOM = 0;
    private final PIDFController controller = new PIDFController(new PIDCoefficients(.005, 0.2, 0.0));
    private MotorGroup vSlides;
    private VSlides() {
    }

    // Initializes the hardware and sets the PID controller
    public void initialize() {
        // The two motors that control the slides
        MotorEx vSlideRight = new MotorEx("vSlideRight");
        MotorEx vSlideLeft = new MotorEx("vSlideLeft");

        vSlideRight.reverse();

        vSlides = new MotorGroup(vSlideLeft, vSlideRight);

        controller.setSetPointTolerance(THRESHOLD);
        controller.setOutputBounds(0, MAX_SLIDE_HEIGHT); //TODO: This is maybe power
    }

    @NonNull
    @Override
    public Command getDefaultCommand() {
        return new HoldPosition(vSlides,
                controller,
                this);
    }

    public Command moveToTopBucket() {
        return new RunToPosition(vSlides, TOP_BUCKET, controller, this);
    }

    public Command moveToBottomBucket() {
        return new RunToPosition(vSlides, BOTTOM_BUCKET, controller, this);
    }

    public Command moveToTopChamber() {
        return new RunToPosition(vSlides, TOP_CHAMBER, controller, this);
    }

    public Command moveToBottomChamber() {
        return new RunToPosition(vSlides, BOTTOM_CHAMBER, controller, this);
    }

    public Command moveToBottom() {
        return new RunToPosition(vSlides, BOTTOM, controller, this);
    }

    public double getTargetPos() {
        return controller.getTarget();
    }

    public double getPos() {
        return vSlides.getCurrentPosition();
    }

    public Command setPower(int i) {
        return new InstantCommand(()-> {
            vSlides.setPower(i);
            return null;
        });
    }
}
