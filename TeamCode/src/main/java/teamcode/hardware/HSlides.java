package teamcode.hardware;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.control.coefficients.PIDCoefficients;
import com.rowanmcalpin.nextftc.core.control.controllers.PIDFController;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.HoldPosition;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.MotorEx;
import com.rowanmcalpin.nextftc.ftc.hardware.controllables.RunToPosition;


@Config
public class HSlides extends Subsystem {
    public static final HSlides INSTANCE = new HSlides();

    public static double OUT = 140;
    public static double IN = -10;
    public PIDFController controller = new PIDFController(new PIDCoefficients(.5, 0.0, 0.0));
    private MotorEx hSlide;

    private HSlides() {
    }

    // Initializes the hardware
    public void initialize() {
        hSlide = new MotorEx("hSlide");
        hSlide.reverse();

        //TODO: Set output bounds
    }

    @NonNull
    @Override
    public Command getDefaultCommand() {
        return new HoldPosition(hSlide,
                controller,
                this);
    }

    public Command slideIn() {
        return new RunToPosition(hSlide,
                IN,
                controller,
                this);
    }

    public Command slideOut() {
        return new RunToPosition(hSlide,
                OUT,
                controller,
                this);
    }

    public double getTargetPos() {
        return controller.getTarget();
    }

    public double getPos() {
        return hSlide.getCurrentPosition();
    }

}