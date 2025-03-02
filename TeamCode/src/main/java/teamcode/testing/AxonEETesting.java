package teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

import teamcode.hardware.Robot;
import teamcode.hardware.StateMachine;
import teamcode.helpers.GamepadEx;

@Disabled
@TeleOp(name = "Axon EE Testing", group = "Testing")
public class AxonEETesting extends LinearOpMode {

    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx();
    StateMachine.State state, lineCap;
    List<StateMachine.State> line;
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);

        waitForStart();

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
            telemetry.addData("Axon pos", robot.transfer.ee.getBigPos());
            telemetry.update();
        }
    }
}
