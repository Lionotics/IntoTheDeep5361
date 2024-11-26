package org.firstinspires.ftc.teamcode.hardware;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class EndEffector {

    public static double CLAW_CLOSE = 0.5, CLAW_OPEN = 0.7;
    public static double ROTATE_UP = 0.65, ROTATE_DOWN = 0.125;

    Servo claw, wrist;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "endEffectorClaw");
        wrist = hwMap.get(Servo.class, "endEffectorPivot");
        rotateDown();
        closeClaw();
    }

    private void setClaw(double pos) {
        claw.setPosition(pos);
    }

    private void setWrist(double pos) {
        wrist.setPosition(pos);
    }

    public void close() {
        setClaw(CLAW_CLOSE);
    }

    public void open() {
        setClaw(CLAW_OPEN);
    }

    public void rotateUp() {
        setWrist(ROTATE_UP);
    }

    public void rotateDown() {
        setWrist(ROTATE_DOWN);
    }

    public Action openClaw() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                open();
                packet.put("Claw State: ", "Open");
                return false;
            }
        };
    }

    public Action closeClaw() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                close();
                packet.put("Claw State: ", "Open");
                return false;
            }
        };
    }

    public Action rotateUpWrist() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                rotateUp();
                packet.put("Wrist State: ", "Up");
                return false;
            }
        };
    }

    public Action rotateDownWrist() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                rotateDown();
                packet.put("Wrist State: ", "Down");
                return false;
            }
        };
    }
}
