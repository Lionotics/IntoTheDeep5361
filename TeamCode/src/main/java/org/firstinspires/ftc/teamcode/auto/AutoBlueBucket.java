package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.Pose2d;
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
    Pose2d initialPose = new Pose2d(38, 63.5, Math.toRadians(90));
    MecanumDrive drive;
    int loop = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        /*telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        robot.init(hardwareMap);
        robot.slides.moveToPosition(RSlides.LiftPositions.BOTTOM);
        robot.ee.rotateDown();
        robot.ee.close();

        // TODO: Read inputs from gamepad 1 while(opModeIsInit())
        waitForStart();
        while (opModeIsActive()) {
            Actions.runBlocking(
                    new SequentialAction(
                            //new SleepAction(delay),
                            new ParallelAction(
                                    robot.slides.slidesMoveTo(RSlides.LiftPositions.TOP_BUCKET),
                                    robot.ee.rotateUpWrist()
                            ),
                            new SequentialAction(
                                    robot.driveTrain.drive(2.75),
                                    new InstantAction(() -> {
                                        robot.driveTrain.stop();
                                    }),
                                    new SleepAction(2),
                                    robot.ee.openClaw(),
                                    new SleepAction(2),
                                    robot.ee.rotateDownWrist(),
                                    robot.slides.slidesMoveTo(RSlides.LiftPositions.BOTTOM)
                            )
                    )
            );
            // Setup for teleop
            robot.intake.init();
            break;
        }*/
    }
}
