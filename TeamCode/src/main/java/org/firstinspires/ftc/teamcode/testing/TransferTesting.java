package org.firstinspires.ftc.teamcode.testing;

import static org.firstinspires.ftc.teamcode.hardware.StateMachine.State.START;

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
@TeleOp(name = "Intake Testing", group = "Testing")
public class TransferTesting extends LinearOpMode {
    Robot robot = Robot.getInstance();
    StateMachine.State state;
    StateMachine.State lineCap;
    List<StateMachine.State> line;
    GamepadEx gp1 = new GamepadEx(), gp2 = new GamepadEx();

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);
        waitForStart();
        robot.transfer.stateMachine.next();
        while (opModeIsActive()) {
            state = robot.transfer.stateMachine.getCurrentState();
            line = robot.transfer.stateMachine.getCurrentLine();
            lineCap = line.get(line.size() - 1);
            gp1.update(gamepad1);

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.transfer.next();
            } else if (gp1.leftBumper.isNewlyPressed()) {
                robot.transfer.previous();
            } else if (gp1.a.isNewlyPressed()) {
                robot.transfer.switchToSpecimen();
            } else if (gp1.b.isNewlyPressed()) {
                robot.transfer.switchToSample();
            }

            telemetry.addData("State", state.name());
            telemetry.addData("Linecap", lineCap.name());
            telemetry.update();
        }
    }
}
