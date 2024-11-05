package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.Robot;

@TeleOp(name="OpenHouseTeleop")
public class OpenHouseTeleop extends LinearOpMode {

    Robot robot = new Robot();
    Gamepad gp1 = new Gamepad();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            robot.driveTrain.drive(gp1.left_stick_y, gp1.left_stick_x, gp1.right_stick_x);
        }
    }
}
