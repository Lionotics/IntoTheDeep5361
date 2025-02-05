package teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class EndEffector {

    @Config
    public static class EEConsts {
        public static double CLAW_CLOSE = 0;
        public static double CLAW_OPEN = .2;
        public static double BIG_PRE_TRANSFER = .1;
        public static double BIG_SPECIMEN = 0;
        public static double BIG_POST_TRANSFER = 0;
        public static double BIG_SAMPLE = 0.45;
        public static double BIG_WALL = 0.75;
        public static double LITTLE_SPECIMEN = 0.15;
        public static double LITTLE_TRANSFER = 0.515;
        public static double LITTLE_SAMPLE = 0;
        public static double LITTLE_WALL = 0.16;
    }

    public Servo claw, bigPivot, littlePivot;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "outtakeClaw");
        bigPivot = hwMap.get(Servo.class, "bigPivot");
        littlePivot = hwMap.get(Servo.class, "littlePivot");
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
