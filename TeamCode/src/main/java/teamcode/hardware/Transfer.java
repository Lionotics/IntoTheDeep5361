package teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Config
public class Transfer {

    private static final Logger log = LoggerFactory.getLogger(Transfer.class);
    public StateMachine stateMachine = new StateMachine();
    public EndEffector ee = new EndEffector();
    public Intake intake = new Intake();

    public void init(HardwareMap hwMap) {
        intake.init(hwMap);
        ee.init(hwMap);
    }

    private void transition(StateMachine.State fromState, StateMachine.State toState) {
        switch (toState) {
            case START:
                break;
            case BARRIER:
                ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.EEConsts.BIG_POST_TRANSFER);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_TRANSFER);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
                intake.setPivot(Intake.IntakeConsts.PIVOT_BARRIER);
                break;
            case HOVERG:
                ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.EEConsts.BIG_POST_TRANSFER);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_TRANSFER);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
                intake.setPivot(Intake.IntakeConsts.PIVOT_GRAB);
                break;
            case GRABG:
                ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.EEConsts.BIG_PRE_TRANSFER);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_TRANSFER);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setClaw(Intake.IntakeConsts.CLAW_CLOSE);
                intake.setPivot(Intake.IntakeConsts.PIVOT_GRAB);
                break;
            case TRANSFER:
                ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_TRANSFER);
                ee.setBigPivot(EndEffector.EEConsts.BIG_PRE_TRANSFER);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                intake.setClaw(Intake.IntakeConsts.CLAW_CLOSE);
                break;
            case SAMPLESCORE:
                switch (fromState) {
                    case TRANSFER:
                        ee.setBigPivot(EndEffector.EEConsts.BIG_POST_TRANSFER);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(350);
                                    ee.setClaw(EndEffector.EEConsts.CLAW_CLOSE);
                                    Thread.sleep(500);
                                    intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
                                    Thread.sleep(750);
                                    ee.setBigPivot(EndEffector.EEConsts.BIG_SAMPLE);
                                    ee.setLittlePivot(EndEffector.EEConsts.LITTLE_SAMPLE);
                                    intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                                    intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                                } catch (InterruptedException e) {
                                    log.error("Failed to handle multi threading{}", Arrays.toString(e.getStackTrace()));
                                }
                            }
                        }.start();
                        break;
                    case BARRIER:
                        ee.setClaw(EndEffector.EEConsts.CLAW_CLOSE);
                        ee.setBigPivot(EndEffector.EEConsts.BIG_SAMPLE);
                        ee.setLittlePivot(EndEffector.EEConsts.LITTLE_SAMPLE);
                        intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                        intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
                        intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                        break;
                }
                break;
            case HOVERW:
                ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                ee.setBigPivot(EndEffector.EEConsts.BIG_WALL);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_WALL);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setClaw(Intake.IntakeConsts.CLAW_CLOSE);
                intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                break;
            case GRABW:
                ee.setClaw(EndEffector.EEConsts.CLAW_CLOSE);
                ee.setBigPivot(EndEffector.EEConsts.BIG_WALL);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_WALL);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
                intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                break;
            case SPECIMENSCORE:
                // Can only come from GRABW
                ee.setClaw(EndEffector.EEConsts.CLAW_CLOSE);
                ee.setBigPivot(EndEffector.EEConsts.BIG_SPECIMEN);
                ee.setLittlePivot(EndEffector.EEConsts.LITTLE_SPECIMEN);
                intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                intake.setClaw(Intake.IntakeConsts.CLAW_OPEN);
                intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
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
