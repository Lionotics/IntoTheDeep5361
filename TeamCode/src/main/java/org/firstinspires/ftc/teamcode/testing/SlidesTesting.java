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


    Robot robot = new Robot();
    public static double LEFT_POWER = .1;
    public static double RIGHT_POWER = .2;


    @Override
    public void runOpMode() {
        FtcDashboard dash = FtcDashboard.getInstance();
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, dash.getTelemetry());

        robot.init(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            robot.slides.setDifferentialLeft(LEFT_POWER);
            robot.slides.setDifferentialRight(RIGHT_POWER);
        }
    }
}
