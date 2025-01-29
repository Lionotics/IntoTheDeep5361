package teamcode.hardware;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Intake {

    public static enum Consts {
        CLAW_CLOSE(0.1), CLAW_OPEN(.6),
        PIVOT_BARRIER(0.65), PIVOT_GRAB(0.95), PIVOT_TRANSFER(0),
        WRIST_UP(0.31), WRIST_DOWN(0);

        public final double pos;

        Consts(double pos) {
            this.pos = pos;
        }
    }

    Servo claw, wrist, pivot;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "intakeClaw");
        wrist = hwMap.get(Servo.class, "intakeWrist");
        pivot = hwMap.get(Servo.class, "intakePivot");
    }

    public void setClaw(Consts pos) {
        claw.setPosition(pos.pos);
    }

    public void setPivot(Consts pos) {
        pivot.setPosition(pos.pos);
    }

    public void setWrist(Consts pos) {
        wrist.setPosition(pos.pos);
    }

    public void setWrist(double pos) {
        wrist.setPosition(pos);
    }


    public static enum WristState {
        WEST(0.6), NORTHWEST(0.5), NORTH(0.31), NORTHEAST(0.15), EAST(0.0);

        public final double pos;

        WristState(double pos) {
            this.pos = pos;
        }
    }

    public WristState currentWristState = WristState.NORTH;

    public void turnWristManualRight() {
        switch (currentWristState) {
            case WEST:
                setWrist(WristState.NORTHWEST.pos);
                currentWristState = WristState.NORTHWEST;
                break;
            case NORTHWEST:
                setWrist(WristState.NORTH.pos);
                currentWristState = WristState.NORTH;
                break;
            case NORTH:
                setWrist(WristState.NORTHEAST.pos);
                currentWristState = WristState.NORTHEAST;
                break;
            case NORTHEAST:
                setWrist(WristState.EAST.pos);
                currentWristState = WristState.EAST;
                break;
            default:
                break;
        }
    }

    public void turnWristManualLeft() {
        switch (currentWristState) {
            case EAST:
                setWrist(WristState.NORTHEAST.pos);
                currentWristState = WristState.NORTHEAST;
                break;
            case NORTHEAST:
                setWrist(WristState.NORTH.pos);
                currentWristState = WristState.NORTH;
                break;
            case NORTH:
                setWrist(WristState.NORTHWEST.pos);
                currentWristState = WristState.NORTHWEST;
                break;
            case NORTHWEST:
                setWrist(WristState.WEST.pos);
                currentWristState = WristState.WEST;
                break;
            default:
                break;
        }
    }

    public double clawPos() {
        return claw.getPosition();
    }

    public double wristPos() {
        return wrist.getPosition();
    }

    public double pivotPos() {
        return pivot.getPosition();
    }
}
