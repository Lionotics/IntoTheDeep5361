package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.StateMachine;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;

import java.util.List;

@Config
@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {

    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx(), gp2 = new GamepadEx();

    @Override
    public void runOpMode() {
        // Implement vision with automatic pivot

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        waitForStart();

        robot.init(hardwareMap);

        robot.transfer.flush();

        while (opModeIsActive()) {
            gp1.update(gamepad1);
            gp2.update(gamepad2);

            StateMachine.State state = robot.transfer.stateMachine.getCurrentState();
            List<StateMachine.State> line = robot.transfer.stateMachine.getCurrentLine();
            StateMachine.State lineCap = line.get(line.size() - 1);

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.transfer.next();
            } else if (gp1.leftBumper.isNewlyPressed()) {
                robot.transfer.previous();
            } else if (gp1.a.isNewlyPressed()) {
                robot.transfer.switchToSpecimen();
            } else if (gp1.b.isNewlyPressed()) {
                robot.transfer.switchToSample();
            }

           if (gp1.x.isNewlyPressed()) {
                robot.transfer.intake.turnWristManualRight();
           } else if (gp1.y.isNewlyPressed()) {
                robot.transfer.intake.turnWristManualLeft();
           }

            if (gp1.dpad_up.isCurrentlyPressed()) {
                robot.vSlides.manualUp();
            } else if (gp1.dpad_down.isCurrentlyPressed()) {
                robot.vSlides.manualDown();
            } else {
                robot.vSlides.hold();
            }

            if (gp1.dpad_right.isCurrentlyPressed()) {
                robot.hSlides.slideExtend();
            } else if (gp1.dpad_left.isCurrentlyPressed()) {
                robot.hSlides.slideRetract();
            } else {
                robot.hSlides.hold();
            }

            robot.driveTrain.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            robot.vSlides.loop();

            telemetry.addData("State", state.name());
            telemetry.addData("Linecap", lineCap.name());
            telemetry.update();
        }
    }

}
