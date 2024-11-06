package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.helpers.PIDController;

@Config
@TeleOp(name="Slides Testing")
public class SlidesTesting extends LinearOpMode {

    //Robot robot = new Robot();
    public static double kP = 0;
    public static double kI = 0;
    public static double kD = 0;

    @Override
    public void runOpMode() {
        FtcDashboard dash = FtcDashboard.getInstance();
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, dash.getTelemetry());

        DcMotor slide = hardwareMap.get(DcMotor.class, "backLeft");
        PIDController controller = new PIDController(kP, kI, kD);
        controller.setSetPoint(1000);

        waitForStart();


        while (opModeIsActive()) {
            while(!controller.atSetPoint()) {
                controller.setPID(kP,kI,kD);
                double power = controller.calculate(slide.getCurrentPosition());
                slide.setPower(power);
                telemetry.addData("Power: ", power);
                telemetry.addData("Position: ", slide.getCurrentPosition());
                telemetry.addData("Target: ", controller.getSetPoint());
                telemetry.update();
            }
            sleep(1000);
            controller.setSetPoint(controller.getSetPoint()+1000);
        }
    }
}
