package org.firstinspires.ftc.teamcode.hardware;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.zyxOrientation;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ftc.GoBildaPinpointDriverRR;
import com.acmerobotics.roadrunner.ftc.LazyImu;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;


public class DriveTrain {
    static final double INCHES_PER_TICK = 340.136;
    public double maxSpeed = 1;
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    public void init(HardwareMap hwMap) {
        frontLeft = hwMap.get(DcMotor.class, "frontLeft");
        frontRight = hwMap.get(DcMotor.class, "frontRight");
        backLeft = hwMap.get(DcMotor.class, "backLeft");
        backRight = hwMap.get(DcMotor.class, "backRight");

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        initIMU(hwMap);
    }

    public void drive(double leftStickY, double leftStickX, double rightStickX, double offset) {
        double y = -leftStickY; // Remember, Y stick value is reversed
        double x = leftStickX;
        double rx = rightStickX;

        // Read inverse IMU heading, as the IMU heading is CW positive
        double botHeading = -imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + Math.toRadians(offset);
        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        setPower(frontLeftPower * maxSpeed, frontRightPower * maxSpeed, backLeftPower * maxSpeed, backRightPower * maxSpeed);
    }

    public void driveRobotCentric(double leftStickY, double leftStickX, double rightStickX) {
        double y = leftStickY; // Remember, Y stick value is reversed
        double x = -leftStickX * 1.1; // Counteract imperfect strafing
        double rx = -rightStickX;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max((Math.abs(y) + Math.abs(x) + Math.abs(rx)), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        setPower(frontLeftPower * maxSpeed, frontRightPower * maxSpeed, backLeftPower * maxSpeed, backRightPower * maxSpeed);
    }

    public void stop() {
        setPower(0, 0, 0, 0);
    }

    public void go() {
        setPower(.1, .1, .1, .1);
    }

    public void initIMU(HardwareMap hwMap) {
        // Retrieve the IMU from the hardware map
        imu = hwMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        imu.initialize(parameters);
        imu.resetYaw();
    }

    public void setPower(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    public Action drive(double time){
        return new Action() {
            private boolean initialized = false;
            private double startTime;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    startTime = System.currentTimeMillis();
                    setPower(0,-.5,-.5,0);
                    initialized = true;
                }
                double timeLeft = startTime + (time * 1000) - System.currentTimeMillis();
                packet.put("Distance Left: ", timeLeft);
                return timeLeft>0;
            }
        };
    }
    public Action driveBack(double time){
        return new Action() {
            private boolean initialized = false;
            private double startTime;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    startTime = System.currentTimeMillis();
                    setPower(0,.5,.5,0);
                    initialized = true;
                }
                double timeLeft = startTime + (time * 1000) - System.currentTimeMillis();
                packet.put("Distance Left: ", timeLeft);
                return timeLeft>0;
            }
        };
    }
}
