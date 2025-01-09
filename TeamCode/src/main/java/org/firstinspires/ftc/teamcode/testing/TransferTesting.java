package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;

@Config
@TeleOp(name="Intake Testing", group="Testing")
public class TransferTesting extends LinearOpMode {
    Robot robot = Robot.getInstance();
    @Override
    public void runOpMode(){
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);
        waitForStart();
        while(opModeIsActive()){

            /*if (gamepad1.a) {
                robot.transfer.setClaw(CLAW_PRESS);
            } else {
                robot.transfer.setClaw(CLAW_RELEASE);
            }
            if (gamepad1.b) {
                robot.transfer.setElbow(ELBOW_PRESS);
            } else {
                robot.transfer.setElbow(ELBOW_RELEASE);
            }
            if (gamepad1.x) {
                robot.transfer.setWrist(WRIST_PRESS);
            } else {
                robot.transfer.setWrist(WRIST_RELEASE);
            }
            if (gamepad1.y) {
                robot.transfer.setShoulder(SHOULDER_PRESS);
            } else {
                robot.transfer.setShoulder(SHOULDER_RELEASE);
            }*/
            //TODO: Match this to the new transfer

            if (gamepad1.dpad_left) {
                robot.hSlides.slide(true);
            } else if (gamepad1.dpad_right) {
                robot.hSlides.slide(false);
            }

//            telemetry.addData("Claw Position", robot.transfer.getClawPosition());
//            telemetry.addData("Elbow Position", robot.transfer.getElbowPosition());
//            telemetry.addData("Wrist Position", robot.transfer.getWristPosition());
//            telemetry.addData("Shoulder Position", robot.transfer.getShoulderPosition());
            telemetry.update();
        }
    }
}
