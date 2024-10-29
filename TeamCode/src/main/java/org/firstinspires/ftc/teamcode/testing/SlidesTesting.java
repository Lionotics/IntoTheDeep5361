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

@Config
@TeleOp(name="Slides Testing")
public class SlidesTesting extends LinearOpMode {

    //Robot robot = new Robot();
    public static double Kp = 0;
    public static double Ki = 0;
    public static double Kd = 0;

    static double Xi = 0;
    public static double Xf = 0;

    double integralSum = 0;

    static double lastError = 0;

    @Override
    public void runOpMode() {
        FtcDashboard dash = FtcDashboard.getInstance();
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, dash.getTelemetry());

        DcMotor slide = hardwareMap.get(DcMotor.class, "backLeft");

        waitForStart();


        while (opModeIsActive()) {

            // Elapsed timer class from SDK, please use it, it's epic
            ElapsedTime timer = new ElapsedTime();

            while (Xf-Xi > 0) {
                // obtain the encoder position
                Xi = slide.getCurrentPosition();
                // calculate the error
                double e = Xf - Xi;

                // rate of change of the error
                double derivative = (e - lastError) / timer.seconds();

                // sum of all error over time
                integralSum = integralSum + (e * timer.seconds());

                double power = (Kp * e) + (Ki * integralSum) + (Kd * derivative);

                slide.setPower(power);

                lastError = e;

                // reset the timer for next time
                timer.reset();

            }

            telemetry.addData("Position: " , Xi);
            telemetry.addData("Target: " , Xf);
            telemetry.update();
        }
    }
}
