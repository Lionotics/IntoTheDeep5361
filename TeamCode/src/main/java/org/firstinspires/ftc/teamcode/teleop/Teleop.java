package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {

    Robot robot = new Robot();
    GamepadEx gp1, gp2;


    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();
        robot.init(hardwareMap);
        robot.intake.init(); //TODO: Add at the end of auton, remove this later
        while (opModeIsActive()) {
            gp1.update(gamepad1);
            gp2.update(gamepad2);

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

            robot.driveTrain.driveRobotCentric(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
            gp1.update(gamepad1);

            telemetry.addData("Index: ", robot.intake.currentState.name());
            telemetry.addData("Horizontal: ", robot.slides.getHorizontalPos());
            telemetry.addData("Vertical: ", robot.slides.getVerticalPos());
            telemetry.update();
        }
    }


}
