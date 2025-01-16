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
            telemetry.addData("Press dpad up (on gp2) to move the vertical slides up", "");
            telemetry.addData("Press dpad down (on gp2) to move the vertical slides down", "");
            telemetry.addData("Press dpad left (on gp2) to move the horizontal slides left", "");
            telemetry.addData("Press dpad right (on gp2) to move the horizontal slides right", "");

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
            } else if (gp1.y.isNewlyPressed()) {
                robot.ee.close();
            }

            if (gp2.dpad_left.isCurrentlyPressed() || gp1.dpad_left.isCurrentlyPressed()) {
                robot.slides.horizontalSlide(-.5);
            } else if (gp2.dpad_right.isCurrentlyPressed() || gp1.dpad_right.isCurrentlyPressed()) {
                robot.slides.horizontalSlide(.5);
            }

            if (gp2.dpad_up.isCurrentlyPressed() || gp1.dpad_up.isCurrentlyPressed()) {
                robot.slides.manualUp();
            } else if (gp2.dpad_down.isCurrentlyPressed() || gp1.dpad_down.isCurrentlyPressed()) {
                telemetry.clear();
                robot.slides.manualDown();
            }

            if (gp2.y.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.TOP_BUCKET);
            } else if (gp2.x.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.BOTTOM_BUCKET);
            } else if (gp2.b.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.TOP_BAR);
            } else if (gp2.a.isNewlyPressed()) {
                robot.slides.moveToPosition(Slides.LiftPositions.BOTTOM);
            } // else if (gp2.back.isNewlyPressed()) {
            // robot.slides.moveToPosition(Slides.LiftPositions.TOP_HANG);
            // }

            if (!gp2.dpad_left.isCurrentlyPressed() && !gp2.dpad_right.isCurrentlyPressed() && !gp2.dpad_up.isCurrentlyPressed() && !gp2.dpad_down.isCurrentlyPressed() && !gp1.dpad_left.isCurrentlyPressed() && !gp1.dpad_right.isCurrentlyPressed() && !gp1.dpad_up.isCurrentlyPressed() && !gp1.dpad_down.isCurrentlyPressed() && !gp2.x.isCurrentlyPressed() && !gp2.y.isCurrentlyPressed() && !gp2.a.isCurrentlyPressed() && !gp2.b.isCurrentlyPressed() && !gp2.back.isCurrentlyPressed()) {
                robot.slides.hold();
            }

            robot.slides.loop();

            telemetry.addData("Slide Mode", robot.slides.getLiftState().name());
            telemetry.addData("Horizontal Pos", robot.slides.getHorizontalPos());
            telemetry.addData("Vertical Pos", robot.slides.getVerticalPos());
            telemetry.addData("Vertical Target", robot.slides.getTargetPos());
            telemetry.addData("Vertical Velocity", robot.slides.getVerticalVelo());
            telemetry.addData("PID Output", robot.slides.getPidPower());
            telemetry.update();

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
