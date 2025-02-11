package teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.helpers.HardwareMonitor;


public class EndEffector {

    Servo claw, bigPivot, littlePivot;
    AnalogInput bigAnalog;
    public HardwareMonitor bigMonitor;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "outtakeClaw");
        bigPivot = hwMap.get(Servo.class, "bigPivot");
        littlePivot = hwMap.get(Servo.class, "littlePivot");
        bigAnalog = hwMap.get(AnalogInput.class, "bigAnalog");

        bigMonitor = new HardwareMonitor(bigPivot, Consts.BIG_THRESH);
    }

    public void setClaw(double pos) {
        claw.setPosition(pos);
    }

    public void setBigPivot(double pos) {
        bigPivot.setPosition(pos);
    }
    public void setLittlePivot(double pos) {
        littlePivot.setPosition(pos);
    }

    //TODO: FIX ACTIONS
    /*public Action openClaw() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                open();
                packet.put("Claw State", "Open");
                return false;
            }
        };
    }

    public Action closeClaw() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                close();
                packet.put("Claw State", "Open");
                return false;
            }
        };
    }

    public Action rotateUpBig() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                bigUp();
                packet.put("Wrist State", "Up");
                return false;
            }
        };
    }

    public Action rotateDownBig() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                bigDown();
                packet.put("Wrist State", "Down");
                return false;
            }
        };
    }
    public Action rotateUpLittle() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                littleUp();
                packet.put("Wrist State", "Up");
                return false;
            }
        };
    }

    public Action rotateDownLittle() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                littleDown();
                packet.put("Wrist State", "Down");
                return false;
            }
        };
    }*/

    public double getClawPos() {
        return claw.getPosition();
    }

    // Values are given by bigAnalog.getVoltage() / 3.3 from 0 - 1. This sets is from -1 - 1
    public double getBigPos() { return (bigAnalog.getVoltage() / 3.3);}

    public double getLittlePos() {
        return littlePivot.getPosition();
    }
}
