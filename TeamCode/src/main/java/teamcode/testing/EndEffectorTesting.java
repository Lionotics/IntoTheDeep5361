package teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.EndEffector;
import teamcode.hardware.Robot;
import teamcode.helpers.GamepadEx;

@Config
@TeleOp(name="End Effector Testing", group = "Testing")
public class EndEffectorTesting extends LinearOpMode {
    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx();

    public static double big = 0;
    public static double small = 0;


    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gp1.a.isCurrentlyPressed()) {
                robot.transfer.ee.bigPivot.setPosition(big);
            }
            if (gp1.b.isCurrentlyPressed()) {
                robot.transfer.ee.littlePivot.setPosition(small);
            }

            telemetry.addData("claw",robot.transfer.ee.clawPos());
            telemetry.addData("big",robot.transfer.ee.bigPos());
            telemetry.addData("little",robot.transfer.ee.littlePos());
            telemetry.update();
            gp1.update(gamepad1);
        }

    }
}
