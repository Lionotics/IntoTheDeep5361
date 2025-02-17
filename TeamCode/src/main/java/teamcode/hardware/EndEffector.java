package teamcode.hardware;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.Servo;
import com.rowanmcalpin.nextftc.core.Subsystem;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.ftc.OpModeData;
import com.rowanmcalpin.nextftc.ftc.hardware.MultipleServosToSeperatePositions;
import com.rowanmcalpin.nextftc.ftc.hardware.ServoToPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class EndEffector extends Subsystem {
    EndEffector instance = new EndEffector();

    Servo claw, bigPivot, littlePivot;
    AnalogInput bigAnalog;

    public void initialize() {
        claw = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, "outtakeClaw");
        bigPivot = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, "bigPivot");
        littlePivot = OpModeData.INSTANCE.getHardwareMap().get(Servo.class, "littlePivot");
        bigAnalog = OpModeData.INSTANCE.getHardwareMap().get(AnalogInput.class, "bigAnalog");
    }

    public Command setClaw(double pos) {
        return new ServoToPosition(claw, pos, this);
    }

    public Command setBigPivot(double pos) {
        return new AxonServoToPosition(bigPivot, pos, bigAnalog, this);
    }

    public Command setLittlePivot(double pos) {
        return new ServoToPosition(littlePivot, pos, this);
    }

    public Command setEE(double clawPos, double bigPos, double littlePos) {
        return new ParallelGroup(new MultipleServosToSeperatePositions(new HashMap<>(Map.of(claw, clawPos, littlePivot, littlePos)), this), setBigPivot(bigPos));
    }

    public double getClawPos() {
        return claw.getPosition();
    }

    // Values are given by bigAnalog.getVoltage() / 3.3 from 0 - 1. This sets is from -1 - 1
    public double getBigPos() {
        return (bigAnalog.getVoltage() / 3.3);
    }

    public double getLittlePos() {
        return littlePivot.getPosition();
    }
}
