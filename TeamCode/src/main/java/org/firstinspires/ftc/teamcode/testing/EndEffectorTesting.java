package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

@TeleOp(name="End Effector Testing", group = "Testing")
public class EndEffectorTesting extends LinearOpMode {
    Robot robot = new Robot();
    GamepadEx gp1 = new GamepadEx();
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
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
