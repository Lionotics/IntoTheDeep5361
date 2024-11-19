package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
@TeleOp(name = "Wheels Testing", group = "Testing")
public class TestWheels extends LinearOpMode {
    Robot robot = new Robot();

    @Override
    public void runOpMode() {
        waitForStart();
        robot.init(hardwareMap);
        telemetry.addData("", "Press dpad to move");
        telemetry.addData("", "Press a to move front left");
        telemetry.addData("", "Press b to move front right");
        telemetry.addData("", "Press y to move back left");
        telemetry.addData("", "Press x to move back right");
        telemetry.update();
        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                robot.driveTrain.driveRobotCentric(.1, 0, 0);
            } else if (gamepad1.dpad_down) {
                robot.driveTrain.driveRobotCentric(-.1, 0, 0);
            } else if (gamepad1.dpad_left) {
                robot.driveTrain.drive(0, -.1, 0);
            } else if (gamepad1.dpad_right) {
                robot.driveTrain.driveRobotCentric(0, .1, 0);
            } else if (gamepad1.a) {
                robot.driveTrain.setPower(.1, 0, 0, 0);
            } else if (gamepad1.b) {
                robot.driveTrain.setPower(0, .1, 0, 0);
            }   else if (gamepad1.y) {
                robot.driveTrain.setPower(0, 0, .1, 0);
            } else if (gamepad1.x) {
                robot.driveTrain.setPower(0, 0, 0, .1);
            } else {
                robot.driveTrain.drive(0, 0, 0);
            }
        }
    }
}
