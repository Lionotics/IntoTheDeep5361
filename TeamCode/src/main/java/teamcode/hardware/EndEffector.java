package teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.helpers.HardwareMonitor;

@Config
public class EndEffector {

    @Config
    public static class EEConsts {
    }

    Servo claw, bigPivot, littlePivot;
    public HardwareMonitor bigMonitor;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "outtakeClaw");
        bigPivot = hwMap.get(Servo.class, "bigPivot");
        littlePivot = hwMap.get(Servo.class, "littlePivot");

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

    public double clawPos() {
        return claw.getPosition();
    }

    public double bigPos() {
        return bigPivot.getPosition();
    }

    public double littlePos() {
        return littlePivot.getPosition();
    }
}
