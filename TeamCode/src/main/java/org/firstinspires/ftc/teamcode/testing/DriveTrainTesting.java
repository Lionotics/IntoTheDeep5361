package org.firstinspires.ftc.teamcode.testing;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Drive Train Testing", group = "Testing")
public class DriveTrainTesting extends LinearOpMode {
    Robot robot = new Robot();

    @Override
    public void runOpMode() {
        waitForStart();
        robot.init(hardwareMap);
        GamepadEx gp1 = new GamepadEx(gamepad1);
        telemetry.addData("", "Regular driving but slow");
        telemetry.update();
        while (opModeIsActive()) {
            robot.driveSubsystem.drive.setMaxSpeed(.1);
            robot.driveSubsystem.drive.driveRobotCentric(gp1.getLeftX(), gp1.getLeftY(), gp1.getRightX());
        }
    }
}
