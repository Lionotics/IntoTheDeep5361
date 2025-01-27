package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.EndEffector;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.VSlides;

import java.util.Arrays;

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

        robot.init(hardwareMap);
        robot.transfer.ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
        robot.transfer.ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
        robot.transfer.intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
        robot.transfer.intake.setWrist(Intake.Consts.WRIST_UP);
        robot.transfer.ee.setClaw(EndEffector.Consts.CLAW_CLOSE);
        sleep(1000);
        robot.transfer.intake.setClaw(Intake.Consts.CLAW_OPEN);
        waitForStart();
        while (opModeIsActive()) {
            Actions.runBlocking(
                    new SequentialAction(
                            //new SleepAction(delay),
                            new ParallelAction(
                                    robot.vSlides.slidesMoveTo(VSlides.LiftPositions.TOP_BUCKET),
                                    new InstantAction(() -> {
                                        robot.transfer.ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                                        robot.transfer.ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                                        robot.transfer.intake.setWrist(Intake.Consts.WRIST_UP);
                                        robot.transfer.intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                                    })
                            ),
                            new SequentialAction(
                                    robot.driveTrain.drive(2.25),
                                    new InstantAction(() -> {
                                        robot.driveTrain.stop();
                                    }),
                                    new SleepAction(2),
                                    new InstantAction(() -> {
                                        robot.transfer.ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                                    }),
                                    new SleepAction(2),
                                    robot.driveTrain.driveBack(.2),
                                    robot.vSlides.slidesMoveTo(VSlides.LiftPositions.BOTTOM)
                            )
                    )
            );
            // Setup for teleop
            break;
        }
    }
}
