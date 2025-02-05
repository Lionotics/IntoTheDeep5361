package teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Intake {

    @Config
    public static class IntakeConsts {
        public static double CLAW_CLOSE = 0.1;
        public static double CLAW_OPEN = .6;
        public static double PIVOT_BARRIER = 0.7;
        public static double PIVOT_GRAB = 0.935;
        public static double PIVOT_TRANSFER = 0.4;
        public static double WRIST_UP = 0.31;
        public static double WRIST_DOWN = 0;
    }

    Servo claw, wrist, pivot;

    public void init(HardwareMap hwMap) {
        claw = hwMap.get(Servo.class, "intakeClaw");
        wrist = hwMap.get(Servo.class, "intakeWrist");
        pivot = hwMap.get(Servo.class, "intakePivot");
    }

    public void setClaw(double pos) {
        claw.setPosition(pos);
    }

    public void setPivot(double pos) {
        pivot.setPosition(pos);
    }

    public void setWrist(double pos) {
        wrist.setPosition(pos);
    }


    public enum WristState {
        WEST(WristPos.WEST), NORTHWEST(WristPos.NORTHWEST), NORTH(WristPos.NORTH), NORTHEAST(WristPos.NORTHEAST), EAST(WristPos.EAST);

        public final double pos;

        WristState(double pos) {
            this.pos = pos;
        }
    }

    public static class WristPos {
        public static double WEST = 0.6;
        public static double NORTHWEST = 0.5;
        public static double NORTH = 0.31;
        public static double NORTHEAST = 0.15;
        public static double EAST = 0.0;
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
