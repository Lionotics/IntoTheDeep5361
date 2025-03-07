package teamcode.testing;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.HSlides;
import teamcode.hardware.Robot;
import teamcode.helpers.GamepadEx;

@Disabled
@TeleOp(name = "HSlides Testing", group = "Testing")
public class HSlidesTesting extends LinearOpMode {


    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx(), gp2 = new GamepadEx();


    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            // Give our custom gamepads the values from the real gamepads
            gp1.update(gamepad1);
            gp2.update(gamepad2);

            if (gp1.dpad_left.isCurrentlyPressed()) {
                robot.hSlides.manualOut();
            } else if (gp1.dpad_right.isCurrentlyPressed()) {
                robot.hSlides.manualIn();
            }

            if (isNoButtonPressed()) {
                robot.hSlides.hold();
            }

            robot.hSlides.loop();

            telemetry.addData("Slide Mode",robot.hSlides.getSlideState().name());
            telemetry.addData("Horizontal Pos", robot.hSlides.getPos());
            telemetry.update();
        }
    }
    private boolean isEitherLeft() {
        return gp1.dpad_left.isCurrentlyPressed() || gp2.dpad_left.isCurrentlyPressed();
    }
    private boolean isEitherRight() {
        return gp1.dpad_right.isCurrentlyPressed() || gp2.dpad_right.isCurrentlyPressed();
    }
    private boolean isAnyButtonPressed() {
        return gp1.a.isCurrentlyPressed() || gp1.b.isCurrentlyPressed() || gp1.x.isCurrentlyPressed() || gp1.y.isCurrentlyPressed();
    }

    private boolean isNoButtonPressed() {
        return !isAnyButtonPressed() && !isEitherLeft() && !isEitherRight() && !gp2.dpad_up.isCurrentlyPressed() && !gp2.dpad_down.isCurrentlyPressed();
    }
}