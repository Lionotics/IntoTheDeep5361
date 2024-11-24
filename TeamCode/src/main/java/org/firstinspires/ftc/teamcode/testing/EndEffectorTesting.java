package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

@TeleOp(name="End Effector Testing", group = "Testing")
public class EndEffectorTesting extends LinearOpMode {
    Robot robot = new Robot();
    GamepadEx gp1 = new GamepadEx();
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gp1.a.isCurrentlyPressed()) {
                robot.ee.open();
            } else {
                robot.ee.close();
            }
            if (gp1.b.isCurrentlyPressed()) {
                robot.ee.rotateUp();
            } else {
                robot.ee.rotateDown();
            }
            gp1.update(gamepad1);
        }

    }
}
