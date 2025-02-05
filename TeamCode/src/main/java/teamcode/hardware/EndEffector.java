package teamcode.hardware;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class EndEffector {

    public static enum Consts {
        CLAW_CLOSE(0), CLAW_OPEN(.2),
        BIG_SPECIMEN(0), BIG_TRANSFER(0), BIG_SAMPLE(0.45), BIG_DROP(0.85), BIG_WALL(0.75),
        LITTLE_SPECIMEN(0.15), LITTLE_TRANSFER(0.515), LITTLE_SAMPLE(0), LITTLE_DROP(0.1), LITTLE_WALL(0.16);

        public double pos;

        Consts(double pos) {
            this.pos = pos;
        }
    }

    public Servo claw, bigPivot, littlePivot;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "outtakeClaw");
        bigPivot = hwMap.get(Servo.class, "bigPivot");
        littlePivot = hwMap.get(Servo.class, "littlePivot");
    }

    public void setClaw(Consts pos) {
        claw.setPosition(pos.pos);
    }

    public void setBigPivot(Consts pos) {
        bigPivot.setPosition(pos.pos);
    }

    public void setLittlePivot(Consts pos) {
        littlePivot.setPosition(pos.pos);
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
