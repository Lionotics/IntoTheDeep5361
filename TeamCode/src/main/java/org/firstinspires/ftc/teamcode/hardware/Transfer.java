package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

@Config
public class Transfer {

    public final StateMachine stateMachine = new StateMachine();
    public EndEffector ee = new EndEffector();
    public Intake intake = new Intake();

    public void init(HardwareMap hwMap) {
        intake.init(hwMap);
        ee.init(hwMap);
    }

    private void transition(StateMachine.State fromState, StateMachine.State toState) {
        switch (toState) {
            case BARRIER1:
                switch (fromState) {
                    case START:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case SAMPLESCORE:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case DROP:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case HOVERW:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case GRABW:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case SPECIMENSCORE:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case HOVERG:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case HOVERG:
                switch (fromState) {
                    case BARRIER1:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case GRABG:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case GRABG:
                switch (fromState) {
                    case HOVERG:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case BARRIER2:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case BARRIER2:
                switch (fromState) {
                    case GRABG:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case TRANSFER:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case TRANSFER:
                switch (fromState) {
                    case BARRIER2:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case SAMPLESCORE:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case DROP:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case HOVERW:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case SAMPLESCORE:
                switch (fromState) {
                    case TRANSFER:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case BARRIER1:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case DROP:
                // Can only come from TRANSFER
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_OPEN);
                intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                break;
            case HOVERW:
                switch (fromState) {
                    case DROP:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case BARRIER1:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case HOVERG:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case GRABG:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case BARRIER2:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    // Can't come from transfer; goto drop instead
                    case SAMPLESCORE:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case GRABW:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case GRABW:
                switch (fromState) {
                    case HOVERW:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                    case SPECIMENSCORE:
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                        break;
                }
                break;
            case SPECIMENSCORE:
                // Can only come from GRABW
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_OPEN);
                intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                break;
        }
    }

    public void next() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.next();
        StateMachine.State toState = stateMachine.getCurrentState();
        transition(fromState, toState);
    }

    public void previous() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.previous();
        StateMachine.State toState = stateMachine.getCurrentState();
        transition(fromState, toState);
    }

    public void switchToSpecimen() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.switchToSpecimen();
        StateMachine.State toState = stateMachine.getCurrentState();
        transition(fromState, toState);
    }

    public void switchToSample() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.switchToSample();
        StateMachine.State toState = stateMachine.getCurrentState();
        transition(fromState, toState);
    }
}
