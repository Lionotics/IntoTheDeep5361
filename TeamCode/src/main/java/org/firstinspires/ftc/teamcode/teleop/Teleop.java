package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Slides;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {

    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx(), gp2 = new GamepadEx();


    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();
        robot.init(hardwareMap);
        robot.intake.init(); //TODO: Add at the end of auton, remove this later
        while (opModeIsActive()) {
            if (gp1.rightBumper.isNewlyPressed()) {
                robot.intake.incrementState();
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

//            if (gp1.dpad_left.isCurrentlyPressed()) {
//                robot.slides.horizontalSlide(.2);
//            } else if (gp1.dpad_right.isCurrentlyPressed()) {
//                robot.slides.horizontalSlide(-.2);
//            }
//
//            if (gp1.dpad_up.isCurrentlyPressed()) {
//                robot.slides.manualUp();
//            } else if (gp1.dpad_down.isCurrentlyPressed()) {
//                robot.slides.manualDown();
//            }

            robot.driveTrain.driveRobotCentric(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            gp1.update(gamepad1);
            gp2.update(gamepad2);

            telemetry.addData("Index: ", robot.intake.currentState.name());
            telemetry.addData("Horizontal: ", robot.slides.getHorizontalPos());
            telemetry.addData("Vertical: ", robot.slides.getVerticalPos());
            telemetry.update();
        }
    }


}
