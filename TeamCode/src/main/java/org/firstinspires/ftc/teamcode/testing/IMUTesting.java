package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;

@TeleOp(name = "Imu", group = "Testing")
public class IMUTesting extends LinearOpMode {
    MecanumDrive drive;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new MecanumDrive(hardwareMap, new Pose2d(0,0,Math.toRadians(180)));
        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        com.qualcomm.robotcore.hardware.IMU.Parameters parameters = new com.qualcomm.robotcore.hardware.IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT));
        imu.initialize(parameters);
        imu.resetYaw();
        waitForStart();
        while (opModeIsActive()) {
            drive.setDrivePowers(new PoseVelocity2d(new Vector2d(.1,.1),.1));
            drive.updatePoseEstimate();
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            telemetry.addData("RR IMU", Math.toDegrees(drive.pose.heading.toDouble()));
            telemetry.addData("RR X", drive.pose.position.x);
            telemetry.addData("RR Y", drive.pose.position.y);
            telemetry.addData("Reg IMU", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.update();
        }
    };

}
