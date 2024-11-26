package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Vertical", group = "Testing")
@Config
public class StraightVertical extends LinearOpMode {
    public static double initialY = 62;
    public static double distance = 32;
    Pose2d initialPose = new Pose2d(57, initialY, Math.toRadians(-90));

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        waitForStart();
        Action trajectoryAction1 = drive.actionBuilder(drive.pose).strafeTo(new Vector2d(57, initialY - distance)).build();
        Actions.runBlocking(trajectoryAction1);
    }
}
