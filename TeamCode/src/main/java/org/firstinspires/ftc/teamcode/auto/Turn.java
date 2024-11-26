package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Turn", group = "Testing")
@Config
public class Turn extends LinearOpMode {
    public static double initialDeg = 57;
    public static double distance = 180;
    Pose2d initialPose = new Pose2d(57, 62, Math.toRadians(initialDeg));

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        waitForStart();
        Action trajectoryAction1 = drive.actionBuilder(drive.pose).turn(Math.toRadians(distance)).build();
        Actions.runBlocking(trajectoryAction1);
    }
}
