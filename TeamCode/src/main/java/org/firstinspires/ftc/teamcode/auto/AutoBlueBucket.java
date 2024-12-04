package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.Slides;

@Autonomous(name = "Auto Blue Bucket", group = "Auto")
public class AutoBlueBucket extends LinearOpMode {
    Robot robot = Robot.getInstance();
    double delay = 0; // seconds
    boolean park = true;
    boolean hang = false;
    Pose2d initialPose = new Pose2d(38, 63.5, Math.toRadians(90));
    MecanumDrive drive;
    int loop = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        drive = new MecanumDrive(hardwareMap, initialPose);
        TrajectoryActionBuilder path1 = drive.actionBuilder(initialPose)
                .strafeToConstantHeading(new Vector2d(46, 57))
                .splineToSplineHeading(new Pose2d(51.5, 51.5, Math.toRadians(225)), Math.toRadians(0));
        Action startToBucket = path1.build();
        robot.init(hardwareMap);
        robot.ee.close();
        // TODO: Read inputs from gamepad 1
        waitForStart();
        Actions.runBlocking(
                new SequentialAction(
                        new SleepAction(delay),
                        new InstantAction(() -> {
                            telemetry.addData("RR IMU", Math.toDegrees(drive.pose.heading.toDouble()));
                            telemetry.addData("RR X", drive.pose.position.x);
                            telemetry.addData("RR Y", drive.pose.position.y);
                        }),
                        startToBucket, //TODO: Why does it turn ccw before moving?
                        new InstantAction(() -> {
                            telemetry.addData("RR IMU", Math.toDegrees(drive.pose.heading.toDouble()));
                            telemetry.addData("RR X", drive.pose.position.x);
                            telemetry.addData("RR Y", drive.pose.position.y);
                        }),
                        robot.slides.slidesMoveTo(Slides.LiftPositions.TOP_BUCKET),
                        robot.ee.rotateUpWrist(),
                        new SleepAction(1),
                        robot.ee.openClaw(),
                        new SleepAction(.5),
                        robot.ee.rotateDownWrist(),
                        new SleepAction(.5),
                        robot.slides.slidesMoveTo(Slides.LiftPositions.BOTTOM)
                )
        );
    }
}
