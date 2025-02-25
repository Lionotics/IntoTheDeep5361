package teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.Robot;
import teamcode.hardware.StateMachine;
import teamcode.helpers.GamepadEx;

import java.util.List;

@TeleOp(name = "Transfer Testing", group = "Testing")
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
        robot.transfer.flush();
        while (opModeIsActive()) {
            state = robot.transfer.stateMachine.getCurrentState();
            line = robot.transfer.stateMachine.getCurrentLine();
            lineCap = line.get(line.size() - 1);
            gp1.update(gamepad1);

            if (gp1.rightBumper.isNewlyPressed()) {
                robot.transfer.next(true);
            } else if (gp1.leftBumper.isNewlyPressed()) {
                robot.transfer.previous(true);
            } else if (gp1.a.isNewlyPressed()) {
                robot.transfer.switchToSpecimen(true);
            } else if (gp1.b.isNewlyPressed()) {
                robot.transfer.switchToSample(true);
            }

            telemetry.addData("State", state.name());
            telemetry.addData("Linecap", lineCap.name());
            telemetry.update();
        }
    }
}
