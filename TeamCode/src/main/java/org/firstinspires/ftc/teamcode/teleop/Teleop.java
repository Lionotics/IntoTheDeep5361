package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.EndEffector;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.StateMachine;
import org.firstinspires.ftc.teamcode.hardware.VSlides;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;
import org.firstinspires.ftc.teamcode.vision.BrickAngleDetector;

import java.util.List;

@Config
@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {

    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx(), gp2 = new GamepadEx();
    //private BrickAngleDetector bad;

    @Override
    public void runOpMode() {
        // Implement vision with automatic pivot

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        //bad = new BrickAngleDetector(true,telemetry);

        waitForStart();

        robot.init(hardwareMap);

        robot.transfer.flush();

        while (opModeIsActive()) {
            gp1.update(gamepad1);
            gp2.update(gamepad2);

            StateMachine.State state = robot.transfer.stateMachine.getCurrentState();
            List<StateMachine.State> line = robot.transfer.stateMachine.getCurrentLine();
            StateMachine.State lineCap = line.get(line.size() - 1);

//            double angle = bad.getAngle();
//            if (Double.isNaN(angle) || angle == 0 || angle == 90 || angle == 180) {continue;}
//            angle = angle/90 - 1;

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.transfer.next();
            } else if (gp1.leftBumper.isNewlyPressed()) {
                robot.transfer.previous();
            }

            if (gp1.right_trigger > 0.5) {
                robot.transfer.switchToSpecimen();
            } else if (gp1.left_trigger > 0.5) {
                robot.transfer.switchToSample();
            }

           if (gp1.x.isNewlyPressed()) {
                robot.transfer.intake.turnWristManualLeft();
           } else if (gp1.b.isNewlyPressed()) {
                robot.transfer.intake.turnWristManualRight();
           }
           /*else {
               if (Double.isNaN(angle)) {
                   robot.transfer.intake.setWrist(angle);
               }
           }*/


            if (gp1.a.isNewlyPressed()) {
                robot.transfer.ee.setClaw(EndEffector.Consts.CLAW_OPEN);
            }

            if (gp1.dpad_up.isCurrentlyPressed() || gp2.dpad_up.isCurrentlyPressed()) {
                robot.vSlides.manualUp();
            } else if (gp1.dpad_down.isCurrentlyPressed() || gp2.dpad_down.isCurrentlyPressed()) {
                robot.vSlides.manualDown();
            } else {
                robot.vSlides.hold();
            }

            if (gp2.rightBumper.isNewlyPressed()) {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
            } else if (gp2.b.isNewlyPressed()) {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
            } else if (gp2.leftBumper.isNewlyPressed()) {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_CHAMBER);
            } else if (gp2.y.isNewlyPressed()) {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_CHAMBER);
            } else if (gp2.a.isNewlyPressed()) {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
            }

            if (gp1.dpad_right.isCurrentlyPressed() || gp2.dpad_right.isCurrentlyPressed()) {
                robot.hSlides.setPower(-1);
            } else if (gp1.dpad_left.isCurrentlyPressed() || gp2.dpad_left.isCurrentlyPressed()) {
                robot.hSlides.setPower(1);
            } else {
                robot.hSlides.hold();
            }

            robot.driveTrain.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x, 0);

            robot.vSlides.loop();

            telemetry.addData("State", state.name());
            telemetry.addData("Linecap", lineCap.name());
            telemetry.addData("Wrist", robot.transfer.intake.currentWristState.name());
            telemetry.addData("Slides", robot.vSlides.getPos());
            telemetry.update();
        }
    }

}
