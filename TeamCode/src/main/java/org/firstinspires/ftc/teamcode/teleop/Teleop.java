package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {

    Robot robot = new Robot();
    GamepadEx gp1 = new GamepadEx();


    @Override
    public void runOpMode() {
        waitForStart();
        robot.init(hardwareMap);
        robot.intake.init(); //TODO: Add at the end of auton, remove this later
        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                robot.slides.verticalSlide(1);
            } else if (gamepad1.dpad_down) {
                robot.slides.verticalSlide(-1);
            }

            if (gamepad1.dpad_left) {
                robot.slides.horizontalSlide(-1);
            } else if (gamepad1.dpad_right) {
                robot.slides.horizontalSlide(1);
            }

            if (!gamepad1.dpad_up && !gamepad1.dpad_down && !gamepad1.dpad_left && !gamepad1.dpad_right) {
                robot.slides.hold();
            }

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.intake.incrementState();
            } else if (gp1.leftBumper.isNewlyPressed()) {
                robot.intake.decrementState();
            }

            if (gp1.b.isNewlyPressed()) {
                robot.intake.turnWristRight();
            } else if (gp1.x.isNewlyPressed()) {
                robot.intake.turnWristLeft();
            }


            robot.driveTrain.driveRobotCentric(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            gp1.update(gamepad1);

            telemetry.addData("Index: ", robot.intake.currentState.name());
            telemetry.update();
        }
    }


}
