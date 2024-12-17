package org.firstinspires.ftc.teamcode.testing;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Vision.BrickAngleDetector;
import org.firstinspires.ftc.teamcode.helpers.GamepadEx;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name="Vision Testing")
public class VisionTesting extends LinearOpMode {

    private VisionPortal visionPortal;
    private BrickAngleDetector bad;

    private GamepadEx gp1 = new GamepadEx();
    @Override
    public void runOpMode() throws InterruptedException {
        boolean isBlue = true;
        while (!gp1.a.isNewlyPressed()) {
            telemetry.addData("Press X for Blue, B for Red", "");
            if (gp1.x.isNewlyPressed()) {
                isBlue = true;
                telemetry.addData("Selected", "Blue");
            } else if (gp1.b.isNewlyPressed()) {
                isBlue = false;
                telemetry.addData("Selected", "Red");
            }
            telemetry.update();
            wait(50);
            gp1.update(gamepad1);
        }
        bad = new BrickAngleDetector(isBlue);

        initVision();
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Brick Angle", bad.getAngle());
            telemetry.update();
        }
        visionPortal.close();
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
