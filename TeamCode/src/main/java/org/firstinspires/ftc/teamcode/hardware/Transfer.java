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
                        break;
                    case SAMPLESCORE:
                        break;
                    case DROP:
                        break;
                    case HOVERW:
                        break;
                    case GRABW:
                        break;
                    case SPECIMENSCORE:
                        break;
                    case HOVERG:
                        break;
                }
                break;
            case HOVERG:
                switch (fromState) {
                    case BARRIER1:
                        break;
                    case GRABG:
                        break;
                }
                break;
            case GRABG:
                switch (fromState) {
                    case HOVERG:
                        break;
                    case BARRIER2:
                        break;
                }
                break;
            case BARRIER2:
                switch (fromState) {
                    case GRABG:
                        break;
                    case TRANSFER:
                        break;
                }
                break;
            case TRANSFER:
                switch (fromState) {
                    case BARRIER2:
                        break;
                    case SAMPLESCORE:
                        break;
                    case DROP:
                        break;
                    case HOVERW:
                        break;
                }
                break;
            case SAMPLESCORE:
                switch (fromState) {
                    case TRANSFER:
                        break;
                    case BARRIER1:
                        break;
                }
                break;
            case DROP:
                // Can only come from TRANSFER
                break;
            case HOVERW:
                switch (fromState) {
                    case DROP:
                        break;
                    case BARRIER1:
                        break;
                    case HOVERG:
                        break;
                    case GRABG:
                        break;
                    case BARRIER2:
                        break;
                    // Can't come from transfer; goto drop instead
                    case SAMPLESCORE:
                        break;
                    case GRABW:
                        break;
                }
                break;
            case GRABW:
                switch (fromState) {
                    case HOVERW:
                        break;
                    case SPECIMENSCORE:
                        break;
                }
                break;
            case SPECIMENSCORE:
                // Can only come from GRABW
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
