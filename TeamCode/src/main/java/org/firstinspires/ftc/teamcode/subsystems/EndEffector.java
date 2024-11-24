package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
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
    }

    private void setClaw(double pos) {claw.setPosition(pos);}
    private void setWrist(double pos) {wrist.setPosition(pos);}

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
}
