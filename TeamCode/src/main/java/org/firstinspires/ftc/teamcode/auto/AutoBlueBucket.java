package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Robot;

@Autonomous(name = "Auto Blue Bucket", group = "Auto")
public class AutoBlueBucket extends LinearOpMode {
    Robot robot = Robot.getInstance();
    double delay = 0; // seconds
    boolean park = true;
    boolean hang = false;

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPose = new Pose2d(47, 62, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        //TrajectoryActionBuilder builder = drive.trajectoryBuilder(initialPose, true);
        robot.init(hardwareMap);
        // TODO: Read inputs from gamepad 1
        waitForStart();
        Actions.runBlocking(new SequentialAction(new SleepAction(delay), new ParallelAction(robot.intake.hoverIntake())));
    }
}
