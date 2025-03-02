package teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.Consts;
import teamcode.hardware.Intake;
import teamcode.hardware.Robot;
import teamcode.helpers.GamepadEx;

@Disabled
@TeleOp(name="Intake Testing", group = "Testing")
public class IntakeTesting extends LinearOpMode {
    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx();
    private Intake intake;
    
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);
        intake = robot.transfer.intake;
        waitForStart();

        while (opModeIsActive()) {
            if (gp1.y.isCurrentlyPressed()) {
                intake.setClaw(Consts.I_CLAW_OPEN);
            } else {
                intake.setClaw(Consts.I_CLAW_CLOSE);
            }

            if (gp1.b.isCurrentlyPressed()) {
                intake.turnWristManualRight();
            } else if (gp1.x.isCurrentlyPressed()) {
                intake.turnWristManualLeft();
            }

            if (gp1.dpad_up.isCurrentlyPressed()) {
                intake.setPivot(Consts.PIVOT_GRAB);
            } else if (gp1.dpad_right.isCurrentlyPressed()){
                intake.setPivot(Consts.PIVOT_BARRIER);
            } else if (gp1.dpad_down.isCurrentlyPressed()){
                intake.setPivot(Consts.PIVOT_TRANSFER);
            }


            telemetry.addData("claw",intake.clawPos());
            telemetry.addData("wrist",intake.wristPos());
            telemetry.addData("pivot",intake.pivotPos());
            telemetry.update();
            gp1.update(gamepad1);
        }

    }
}
