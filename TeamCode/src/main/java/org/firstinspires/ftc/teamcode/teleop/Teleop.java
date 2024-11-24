package org.firstinspires.ftc.teamcode.teleop;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {

    Robot robot = new Robot();
    GamepadEx gp1 = new GamepadEx(gamepad1);
    GamepadEx gp2 = new GamepadEx(gamepad2);


    @Override
    public void runOpMode() {
        waitForStart();
        robot.init(hardwareMap);
        robot.intake.init(); //TODO: Add at the end of auton, remove this later
        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                robot.slides.verticalSlide(.75);
            } else if (gamepad1.dpad_down) {
                robot.slides.verticalSlide(-.75);
            }

            if (gamepad1.dpad_left) {
                robot.slides.horizontalSlide(-.5);
            } else if (gamepad1.dpad_right) {
                robot.slides.horizontalSlide(.5);
            }

            if (!gamepad1.dpad_up && !gamepad1.dpad_down && !gamepad1.dpad_left && !gamepad1.dpad_right) {
                robot.slides.hold();
            }

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.intake.incrementState(this);
            } else if (gp1.leftBumper.isNewlyPressed()) {
                robot.intake.decrementState();
            }

            if (gp1.x.isNewlyPressed()) {
                robot.intake.turnWristRight();
            } else if (gp1.b.isNewlyPressed()) {
                robot.intake.turnWristLeft();
            }

            if (gp1.a.isNewlyPressed()) {
                robot.ee.open();
            }

            robot.driveSubsystem.drive.driveFieldCentric(gp1.getLeftX(), gp1.getLeftY(), gp1.getRightX(), robot.driveSubsystem.imu.getHeading());

            telemetry.addData("Index: ", robot.intake.currentState.name());
            telemetry.addData("Horizontal: ", robot.slides.getHorizontalPos());
            telemetry.addData("Vertical: ", robot.slides.getVerticalPos());
            telemetry.addData("Slides: ", robot.slides.getSlidesPos());
            telemetry.update();
        }
    }


}
