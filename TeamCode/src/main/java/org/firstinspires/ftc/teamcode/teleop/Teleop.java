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

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.intake.incrementState(this);
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

            telemetry.addData("State: ", robot.intake.currentState.name());
            telemetry.update();
        }
    }


}
