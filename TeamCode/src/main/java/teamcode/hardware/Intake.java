package teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.utility.NullCommand;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.MultipleServosToSeperatePositions;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;

import java.util.HashMap;
import java.util.Map;

public class Intake extends Subsystem {
    public static final Intake INSTANCE = new Intake();
    public WristState currentWristState = WristState.NORTH;
    Servo claw, wrist, pivot;

    private Intake() {
    }

    public void initialize() {
        claw = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, "intakeClaw");
        wrist = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, "intakeWrist");
        pivot = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, "intakePivot");
    }

    public Command setClaw(double pos) {
        return new ServoToPosition(claw, pos, this);
    }

    public Command setPivot(double pos) {
        return new ServoToPosition(pivot, pos, this);
    }

    public Command setIntake(double clawPos, double pivotPos, WristState wristPos) {
        return new ParallelGroup(new MultipleServosToSeperatePositions(new HashMap<>(Map.of(claw, clawPos, pivot, pivotPos)), this), turnWristTo(wristPos));
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

    public Command turnWristManualRight() {
        switch (currentWristState) {
            case WEST:
                return turnWristTo(WristState.NORTHWEST);
            case NORTHWEST:
                return turnWristTo(WristState.NORTH);
            case NORTH:
                return turnWristTo(WristState.NORTHEAST);
            case NORTHEAST:
                return turnWristTo(WristState.EAST);
        }
        return new NullCommand();
    }

    public Command turnWristManualLeft() {
        switch (currentWristState) {
            case EAST:
                return turnWristTo(WristState.NORTHEAST);
            case NORTHEAST:
                return turnWristTo(WristState.NORTH);
            case NORTH:
                return turnWristTo(WristState.NORTHWEST);
            case NORTHWEST:
                return turnWristTo(WristState.WEST);
        }
        return new NullCommand();
    }

    public Command turnWristTo(WristState direction) {
        currentWristState = direction;
        double pos = 0;
        switch (direction) {
            case EAST:
                pos = Consts.W_EAST;
                break;
            case NORTHEAST:
                pos = Consts.W_NORTHEAST;
                break;
            case NORTH:
                pos = Consts.W_NORTH;
                break;
            case NORTHWEST:
                pos = Consts.W_NORTHWEST;
                break;
            case WEST:
                pos = Consts.W_WEST;
                break;
        }
        return new ServoToPosition(wrist, pos, this);
    }

    public enum WristState {
        WEST, NORTHWEST, NORTH, NORTHEAST, EAST
    }
}
