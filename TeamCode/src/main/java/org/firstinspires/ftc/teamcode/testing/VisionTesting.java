package org.firstinspires.ftc.teamcode.testing;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.BrickAngleDetector;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name="Vision Testing")
public class VisionTesting extends LinearOpMode {

    public VisionPortal visionPortal;
    private BrickAngleDetector bad;
    public VisionPortal.CameraState cs;

    private GamepadEx gp1 = new GamepadEx();
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        boolean isBlue = true;
        bad = new BrickAngleDetector(isBlue,telemetry);

        initVision();

        waitForStart();
        while (opModeIsActive()) {
            cs = visionPortal.getCameraState();
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
        cs = visionPortal.getCameraState();
    }
}
