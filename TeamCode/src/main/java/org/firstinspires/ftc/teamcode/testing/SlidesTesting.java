package org.firstinspires.ftc.teamcode.testing;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Slides;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;
import org.firstinspires.ftc.teamcode.helpers.PIDController;


@Config
@TeleOp(name="Slides Testing")
public class SlidesTesting extends LinearOpMode {


    Robot robot = new Robot();
    GamepadEx gp1, gp2;


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

            if (isEitherLeft()) {
                robot.slides.horizontalSlide();
            } else if (isEitherRight()) {
                robot.slides.horizontalSlide();
            }

            if (gp2.dpad_up.isCurrentlyPressed()) {
                robot.slides.manualUp();
            } else if (gp2.dpad_down.isCurrentlyPressed()) {
                robot.slides.manualDown();
            }

            if (gp1.a.isNewlyPressed()) {
               robot.slides.moveToPosition(Slides.LiftPositions.TOP_BUCKET);
            } else if (gp1.b.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.BOTTOM_BUCKET);
            } else if (gp1.x.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.TOP_BAR);
            } else if (gp1.y.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.BOTTOM_BAR);
            }

            if (isNoButtonPressed()) {
                robot.slides.hold();
            }

            robot.slides.loop();

            telemetry.addData("Index: ", robot.intake.currentState.name());
            telemetry.addData("Horizontal: ", robot.slides.getHorizontalPos());
            telemetry.addData("Vertical: ", robot.slides.getVerticalPos());
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
