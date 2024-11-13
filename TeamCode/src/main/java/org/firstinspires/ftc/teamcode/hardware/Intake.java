package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {
    private Servo claw, wrist, elbow, shoulder;
    private enum state {
        HOVER, CLOSE_CLAW, HARVEST, DUMP
    }

    public void init(HardwareMap hwMap) {
        claw = hwMap.servo.get("intakeClaw");
        wrist = hwMap.servo.get("clawRotate");
        elbow = hwMap.servo.get("intakeRotate");
        shoulder = hwMap.servo.get("intakePivot");
    }

    public void setClaw(double position) {
        claw.setPosition(position);
    }

    public double getClawPosition() {
        return claw.getPosition();
    }
    public void setWrist(double position) {
        wrist.setPosition(position);
    }
    public double getWristPosition() {
        return wrist.getPosition();
    }
    public void setElbow(double position) {
        elbow.setPosition(position);
    }
    public double getElbowPosition() {
        return elbow.getPosition();
    }
    public void setShoulder(double position) {
        shoulder.setPosition(position);
    }
    public double getShoulderPosition() {
        return shoulder.getPosition();
    }
}
