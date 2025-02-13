package teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.Robot;
import teamcode.helpers.GamepadEx;

@TeleOp(name = "HSlides Testing")
public class HSlidesTesting extends LinearOpMode {
    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx();
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gp1.dpad_right.isCurrentlyPressed()) {
                robot.hSlides.setPower(1);
            } else if (gp1.dpad_left.isCurrentlyPressed()) {
                robot.hSlides.setPower(-1);
            }

            telemetry.addData("Pos", robot.hSlides.getPosition());
            telemetry.update();

            gp1.update(gamepad1);
        }
    }
}
