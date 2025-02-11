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
                ee.setClaw(Consts.E_CLAW_OPEN);
                ee.setBigPivot(Consts.BIG_TRANSFER);
                ee.setLittlePivot(Consts.LITTLE_TRANSFER);
                intake.alignWrist();
                intake.setClaw(Consts.I_CLAW_OPEN);
                intake.setPivot(Consts.PIVOT_BARRIER);
                break;
            case HOVERG:
                ee.setClaw(Consts.E_CLAW_OPEN);
                ee.setBigPivot(Consts.BIG_TRANSFER);
                ee.setLittlePivot(Consts.LITTLE_TRANSFER);
                intake.alignWrist();
                intake.setClaw(Consts.I_CLAW_OPEN);
                intake.setPivot(Consts.PIVOT_GRAB);
                break;
            case GRABG:
                ee.setClaw(Consts.E_CLAW_OPEN);
                ee.setBigPivot(Consts.BIG_GRAB);
                ee.setLittlePivot(Consts.LITTLE_TRANSFER);
                intake.setClaw(Consts.I_CLAW_CLOSE);
                intake.setPivot(Consts.PIVOT_GRAB);
                break;
            case TRANSFER:
                ee.setClaw(Consts.E_CLAW_OPEN);
                ee.setLittlePivot(Consts.LITTLE_TRANSFER);
                ee.setBigPivot(Consts.BIG_GRAB);
                intake.currentWristState = Intake.WristState.NORTH;
                intake.alignWrist();
                intake.setPivot(Consts.PIVOT_TRANSFER);
                intake.setClaw(Consts.I_CLAW_CLOSE);
                break;
            case SAMPLESCORE:
                switch (fromState) {
                    case TRANSFER:
                        ee.setBigPivot(Consts.BIG_TRANSFER);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (!ee.bigMonitor.isWithinThreshold(Consts.BIG_TRANSFER)) {
                                        Thread.sleep(10);
                                    }
                                    ee.setClaw(Consts.E_CLAW_CLOSE);
                                    Thread.sleep(500);
                                    intake.setClaw(Consts.I_CLAW_OPEN);
                                    Thread.sleep(750);
                                    ee.setBigPivot(Consts.BIG_SAMPLE);
                                    ee.setLittlePivot(Consts.LITTLE_SAMPLE);
                                    intake.alignWrist();
                                    intake.setPivot(Consts.PIVOT_TRANSFER);
                                } catch (InterruptedException e) {
                                    log.error("Failed to handle multi threading{}", Arrays.toString(e.getStackTrace()));
                                }
                            }
                        }.start();
                        break;
                    case BARRIER:
                        ee.setClaw(Consts.E_CLAW_CLOSE);
                        ee.setBigPivot(Consts.BIG_SAMPLE);
                        ee.setLittlePivot(Consts.LITTLE_SAMPLE);
                        intake.alignWrist();
                        intake.setClaw(Consts.I_CLAW_OPEN);
                        intake.setPivot(Consts.PIVOT_TRANSFER);
                        break;
                }
                break;
            case HOVERW:
                ee.setClaw(Consts.E_CLAW_OPEN);
                ee.setBigPivot(Consts.BIG_WALL);
                ee.setLittlePivot(Consts.LITTLE_WALL);
                intake.alignWrist();
                intake.setClaw(Consts.I_CLAW_CLOSE);
                intake.setPivot(Consts.PIVOT_TRANSFER);
                break;
            case GRABW:
                ee.setClaw(Consts.E_CLAW_CLOSE);
                ee.setBigPivot(Consts.BIG_WALL);
                ee.setLittlePivot(Consts.LITTLE_WALL);
                intake.alignWrist();
                intake.setClaw(Consts.I_CLAW_OPEN);
                intake.setPivot(Consts.PIVOT_TRANSFER);
                break;
            case SPECIMENSCORE:
                // Can only come from GRABW
                ee.setClaw(Consts.E_CLAW_CLOSE);
                ee.setBigPivot(Consts.BIG_SPECIMEN);
                ee.setLittlePivot(Consts.LITTLE_SPECIMEN);
                intake.alignWrist();
                intake.setClaw(Consts.I_CLAW_OPEN);
                intake.setPivot(Consts.PIVOT_TRANSFER);
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
