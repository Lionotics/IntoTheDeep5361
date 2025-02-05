package teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.Intake;
import teamcode.hardware.Robot;
import teamcode.helpers.GamepadEx;

@TeleOp(name="Intake Testing", group = "Testing")
public class IntakeTesting extends LinearOpMode {
    Robot robot = Robot.getInstance();
    GamepadEx gp1 = new GamepadEx();
    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gp1.a.isCurrentlyPressed()) {
                robot.transfer.intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
            } else {
                robot.transfer.intake.setClaw(Intake.IntakeConsts.CLAW_CLOSE);
            }

            if (gp1.b.isCurrentlyPressed()) {
                robot.transfer.intake.setWrist(Intake.IntakeConsts.WRIST_DOWN);
            } else {
                robot.transfer.intake.setWrist(Intake.IntakeConsts.WRIST_UP);
            }

            if (gp1.dpad_up.isCurrentlyPressed()) {
                robot.transfer.intake.setPivot(Intake.IntakeConsts.PIVOT_GRAB);
            } else if (gp1.dpad_right.isCurrentlyPressed()){
                robot.transfer.intake.setPivot(Intake.IntakeConsts.PIVOT_BARRIER);
            } else if (gp1.dpad_down.isCurrentlyPressed()){
                robot.transfer.intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
            }


            telemetry.addData("claw",robot.transfer.intake.clawPos());
            telemetry.addData("wrist",robot.transfer.intake.wristPos());
            telemetry.addData("pivot",robot.transfer.intake.pivotPos());
            telemetry.update();
            gp1.update(gamepad1);
        }

    }
}
