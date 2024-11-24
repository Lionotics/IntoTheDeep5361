package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

@Config
@TeleOp(name="Intake Testing", group="Testing")
public class IntakeTesting extends LinearOpMode {
    public static double SHOULDER_PRESS = 0, SHOULDER_RELEASE = 0;
    public static double ELBOW_PRESS = 0, ELBOW_RELEASE = 0;
    public static double WRIST_PRESS = 0, WRIST_RELEASE = 0;
    public static double CLAW_PRESS = 0, CLAW_RELEASE = 0;
    Robot robot = new Robot();
    @Override
    public void runOpMode(){
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);
        waitForStart();
        while(opModeIsActive()){

            if (gamepad1.a) {
                robot.intake.setClaw(CLAW_PRESS);
            } else {
                robot.intake.setClaw(CLAW_RELEASE);
            }
            if (gamepad1.b) {
                robot.intake.setElbow(ELBOW_PRESS);
            } else {
                robot.intake.setElbow(ELBOW_RELEASE);
            }
            if (gamepad1.x) {
                robot.intake.setWrist(WRIST_PRESS);
            } else {
                robot.intake.setWrist(WRIST_RELEASE);
            }
            if (gamepad1.y) {
                robot.intake.setShoulder(SHOULDER_PRESS);
            } else {
                robot.intake.setShoulder(SHOULDER_RELEASE);
            }

            if (gamepad1.dpad_left) {
                robot.slides.horizontalSlide();
            } else if (gamepad1.dpad_right) {
                robot.slides.horizontalSlide();
            }

            telemetry.addData("Claw Position", robot.intake.getClawPosition());
            telemetry.addData("Elbow Position", robot.intake.getElbowPosition());
            telemetry.addData("Wrist Position", robot.intake.getWristPosition());
            telemetry.addData("Shoulder Position", robot.intake.getShoulderPosition());
            telemetry.update();
        }
    }
}
