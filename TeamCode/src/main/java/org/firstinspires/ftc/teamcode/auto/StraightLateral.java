package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Lateral", group = "Auto")
@Config
public class StraightLateral extends LinearOpMode {
    public static double initialX = 57;
    public static double distance = 32;
    Pose2d initialPose = new Pose2d(initialX, 62, Math.toRadians(-90));

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        waitForStart();
        Action trajectoryAction1 = drive.actionBuilder(drive.pose)
                .strafeTo(new Vector2d(initialX-distance, 62))
                .build();
        Actions.runBlocking(trajectoryAction1);
    }
}
