package teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import teamcode.helpers.HardwareMonitor;

public class Intake {
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
        WEST, NORTHWEST, NORTH, NORTHEAST, EAST
    }

    public WristState currentWristState = WristState.NORTH;

    public void alignWrist() {
        setWrist(Consts.W_NORTH);
        currentWristState = WristState.NORTH;
    }

    public void turnWristManualRight() {
        switch (currentWristState) {
            case WEST:
                setWrist(Consts.W_NORTHWEST);
                currentWristState = WristState.NORTHWEST;
                break;
            case NORTHWEST:
                setWrist(Consts.W_NORTH);
                currentWristState = WristState.NORTH;
                break;
            case NORTH:
                setWrist(Consts.W_NORTHEAST);
                currentWristState = WristState.NORTHEAST;
                break;
            case NORTHEAST:
                setWrist(Consts.W_EAST);
                currentWristState = WristState.EAST;
                break;
            default:
                break;
        }
    }

    public void turnWristManualLeft() {
        switch (currentWristState) {
            case EAST:
                setWrist(Consts.W_NORTHEAST);
                currentWristState = WristState.NORTHEAST;
                break;
            case NORTHEAST:
                setWrist(Consts.W_NORTH);
                currentWristState = WristState.NORTH;
                break;
            case NORTH:
                setWrist(Consts.W_NORTHWEST);
                currentWristState = WristState.NORTHWEST;
                break;
            case NORTHWEST:
                setWrist(Consts.W_WEST);
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
