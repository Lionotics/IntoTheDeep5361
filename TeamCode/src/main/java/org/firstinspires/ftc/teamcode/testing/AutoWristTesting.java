package org.firstinspires.ftc.teamcode.testing;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Vision.BrickAngleDetector;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name = "Auto Wrist Testing", group = "Testing")
public class AutoWristTesting extends LinearOpMode {
    VisionPortal visionPortal;
    BrickAngleDetector bad;
    double prevAngle = 90;

    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        bad = new BrickAngleDetector(true,telemetry);

        initVision();

        waitForStart();
        Robot robot = Robot.getInstance();
        robot.init(hardwareMap);


        while (opModeIsActive()) {
            double angle = bad.getAngle();
            if (angle == Double.NaN || angle == 0 || angle == 90 || angle == 180) {continue;}
            robot.transfer.intake.setWrist(angle/90 - 1);
            prevAngle = angle;
            telemetry.addData("Brick Angle", bad.getAngle());
            telemetry.update();
        }


//        visionPortal.close();
    }

    private void initVision() {
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        builder.setCameraResolution(new Size(640, 480));
        builder.enableLiveView(true);
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);
        builder.addProcessor(bad);
        visionPortal = builder.build();
        visionPortal.setProcessorEnabled(bad, true);
    }
}