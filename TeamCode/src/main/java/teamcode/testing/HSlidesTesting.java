package teamcode.testing;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.HSlides;
import teamcode.hardware.Robot;
import teamcode.helpers.GamepadEx;

@TeleOp(name = "HSlides Testing", group = "Testing")
public class HSlidesTesting extends LinearOpMode {


    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx(), gp2 = new GamepadEx();


    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetry.addData("Press dpad up (on gp2) to move the vertical slides up", "");
        telemetry.addData("Press dpad down (on gp2) to move the vertical slides down", "");
        telemetry.addData("Press dpad left (on either gp) to move the horizontal slides left", "");
        telemetry.addData("Press dpad right (on either gp) to move the horizontal slides right", "");

        telemetry.addData("Press a (on gp1) to move the vertical slides to the top bucket", "");
        telemetry.addData("Press b (on gp1) to move the vertical slides to the bottom bucket", "");
        telemetry.addData("Press x (on gp1) to move the vertical slides to the top bar", "");
        telemetry.addData("Press y (on gp1) to move the vertical slides to the bottom bar", "");

        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            // Give our custom gamepads the values from the real gamepads
            gp1.update(gamepad1);
            gp2.update(gamepad2);

            if (gp1.dpad_left.isCurrentlyPressed()) {
                Log.d("Teamcode", "Up pressed");
                robot.hSlides.manualOut();
            } else if (gp1.dpad_right.isCurrentlyPressed()) {
                robot.hSlides.manualIn();
            }

            if (gp1.a.isNewlyPressed()) {
                robot.hSlides.moveToPosition(HSlides.SlidePositions.OUT);
            } else if (gp1.b.isNewlyPressed()) {
                robot.hSlides.moveToPosition(HSlides.SlidePositions.IN);
            }

            if (isNoButtonPressed()) {
                robot.hSlides.hold();
            }

            robot.hSlides.loop();

            telemetry.addData("Slide Mode",robot.hSlides.getSlideState().name());
            telemetry.addData("Horizontal Pos", robot.hSlides.getPos());
            telemetry.addData("Horizontal Target", robot.hSlides.getTargetPos());
            telemetry.addData("PID Output", robot.hSlides.getPidPower());
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
