package teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import teamcode.hardware.Robot;
import teamcode.helpers.PIDController;

@Disabled
@Config
@TeleOp(name="PID Testing", group = "Testing")
public class PIDTesting extends LinearOpMode {
    public static double kP = 0.0006, kI = 0.5, kD = 0, THRESHOLD = 25, setPoint;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Robot robot = Robot.getInstance();
        robot.init(hardwareMap);
        PIDController controller = new PIDController(kP, kI, kD);

        waitForStart();
        /*while (opModeIsActive()){
            controller.setPID(kP, kI, kD);
            controller.setTolerance(THRESHOLD);
            controller.setSetPoint(setPoint);
            double pow = controller.calculate(robot.slides.getVerticalPos());
            pow = (pow < 0) ? pow * .5 : pow;
            if (!controller.atSetPoint()) {
                robot.slides.verticalSlide(pow);
            } else {
                robot.slides.hold();
            }
            telemetry.addData("Power", pow);
            telemetry.addData("Position", robot.slides.getVerticalPos());
            telemetry.addData("Velocity", robot.slides.getVerticalVelo());
            telemetry.update();
        }*/
    }
}
