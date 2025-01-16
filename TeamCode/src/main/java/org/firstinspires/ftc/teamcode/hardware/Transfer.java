package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class Transfer {

    public StateMachine stateMachine = new StateMachine();
    public EndEffector ee = new EndEffector();
    public Intake intake = new Intake();

    public void init(HardwareMap hwMap) {
        intake.init(hwMap);
        ee.init(hwMap);
    }

    private void transition(StateMachine.State fromState, StateMachine.State toState) {
        switch (toState) {
            case BARRIER1:
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_OPEN);
                intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                break;
            case HOVERG:
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_OPEN);
                intake.setPivot(Intake.Consts.PIVOT_GRAB);
                break;
            case GRABG:
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_CLOSE);
                intake.setPivot(Intake.Consts.PIVOT_GRAB);
                break;
            case BARRIER2:
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_CLOSE);
                intake.setPivot(Intake.Consts.PIVOT_BARRIER);
                break;
            case TRANSFER:
                switch (fromState) {
                    case BARRIER2:
                        intake.setClaw(Intake.Consts.CLAW_CLOSE);
                        ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                        ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                        break;
                    case SAMPLESCORE:
                        ee.setBigPivot(EndEffector.Consts.BIG_TRANSFER);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_TRANSFER);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    intake.setClaw(Intake.Consts.CLAW_CLOSE);
                                    Thread.sleep(750);
                                    ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.run();
                        break;
                }
                break;
            case SAMPLESCORE:
                switch (fromState) {
                    case TRANSFER:
                        ee.setClaw(EndEffector.Consts.CLAW_CLOSE);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    intake.setClaw(Intake.Consts.CLAW_OPEN);
                                    Thread.sleep(750);
                                    ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                                    ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                                    intake.setWrist(Intake.Consts.WRIST_UP);
                                    intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.run();
                        break;
                    case BARRIER1:
                        ee.setClaw(EndEffector.Consts.CLAW_CLOSE);
                        ee.setBigPivot(EndEffector.Consts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.Consts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.Consts.WRIST_UP);
                        intake.setClaw(Intake.Consts.CLAW_OPEN);
                        intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                        break;
                }
                break;
            case DROP:
                // Can only come from TRANSFER
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_DROP);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_DROP);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setPivot(Intake.Consts.PIVOT_GRAB);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(750);
                            intake.setClaw(Intake.Consts.CLAW_OPEN);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.run();
                break;
            case HOVERW:
                ee.setClaw(EndEffector.Consts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.Consts.BIG_WALL);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_WALL);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_CLOSE);
                intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                break;
            case GRABW:
                ee.setClaw(EndEffector.Consts.CLAW_CLOSE);
                ee.setBigPivot(EndEffector.Consts.BIG_WALL);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_WALL);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_OPEN);
                intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
                break;
            case SPECIMENSCORE:
                // Can only come from GRABW
                ee.setClaw(EndEffector.Consts.CLAW_CLOSE);
                ee.setBigPivot(EndEffector.Consts.BIG_SPECIMEN);
                ee.setLittlePivot(EndEffector.Consts.LITTLE_SPECIMEN);
                intake.setWrist(Intake.Consts.WRIST_UP);
                intake.setClaw(Intake.Consts.CLAW_OPEN);
                intake.setPivot(Intake.Consts.PIVOT_TRANSFER);
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

    public void flush() {
        stateMachine = new StateMachine();
    }
}
