package teamcode.hardware;

import com.rowanmcalpin.nextftc.core.SubsystemGroup;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.core.command.utility.conditionals.BlockingSwitchCommand;
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kotlin.Pair;

public class Transfer extends SubsystemGroup {
    private static final Logger log = LoggerFactory.getLogger(Transfer.class);
    public static final Transfer INSTANCE = new Transfer();
    public final StateMachine stateMachine = new StateMachine();
    public final Intake intake;
    public final EndEffector ee;

    private Transfer() {
        super(Intake.INSTANCE, EndEffector.INSTANCE);
        this.intake = Intake.INSTANCE;
        this.ee = EndEffector.INSTANCE;
    }

    public void initialize() {
        intake.initialize();
        ee.initialize();
    }

    private Command transition(StateMachine.State fromState, StateMachine.State toState) {
        return new BlockingSwitchCommand(
                () -> toState,
                new Pair[]{
                        new Pair<>(StateMachine.State.BARRIER,
                                new ParallelGroup(
                                        intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_BARRIER, Intake.WristState.NORTH),
                                        new SequentialGroup(
                                                new Delay(1.0),
                                                ee.setEE(Consts.E_CLAW_OPEN, Consts.BIG_TRANSFER, Consts.LITTLE_TRANSFER)
                                        )
                                )
                        ),
                        new Pair<>(StateMachine.State.HOVERG,
                                new ParallelGroup(
                                        ee.setEE(Consts.E_CLAW_OPEN, Consts.BIG_TRANSFER, Consts.LITTLE_TRANSFER),
                                        intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_GRAB, Intake.WristState.NORTH)
                                )
                        ),
                        new Pair<>(StateMachine.State.GRABG,
                                new ParallelGroup(
                                        ee.setEE(Consts.E_CLAW_OPEN, Consts.BIG_GRAB, Consts.LITTLE_TRANSFER),
                                        intake.setIntake(Consts.I_CLAW_CLOSE, Consts.PIVOT_GRAB, Intake.WristState.NORTH)
                                )
                        ),
                        new Pair<>(StateMachine.State.TRANSFER,
                                new ParallelGroup(
                                        ee.setEE(Consts.E_CLAW_OPEN, Consts.BIG_GRAB, Consts.LITTLE_TRANSFER),
                                        intake.setIntake(Consts.I_CLAW_CLOSE, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
                                )
                        ),
                        new Pair<>(StateMachine.State.SAMPLESCORE,
                                new BlockingSwitchCommand(
                                        () -> fromState,
                                        new Pair[]{
                                                new Pair<>(StateMachine.State.TRANSFER,
                                                        new SequentialGroup(
                                                                ee.setBigPivot(Consts.BIG_TRANSFER),
                                                                ee.setClaw(Consts.E_CLAW_CLOSE),
                                                                new Delay(0.5),
                                                                new ParallelGroup(
                                                                        intake.setClaw(Consts.I_CLAW_OPEN),
                                                                        ee.setBigPivot(Consts.BIG_SAMPLE),
                                                                        ee.setLittlePivot(Consts.LITTLE_SAMPLE),
                                                                        intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
                                                                )
                                                        )
                                                ),
                                                new Pair<>(StateMachine.State.BARRIER,
                                                        new ParallelGroup(
                                                                ee.setEE(Consts.E_CLAW_CLOSE, Consts.BIG_SAMPLE, Consts.LITTLE_SAMPLE),
                                                                intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
                                                        )
                                                )}, null
                                )
                        ),
                        new Pair<>(StateMachine.State.HOVERW,
                                new ParallelGroup(
                                        ee.setEE(Consts.E_CLAW_OPEN, Consts.BIG_WALL, Consts.LITTLE_WALL),
                                        intake.setIntake(Consts.I_CLAW_CLOSE, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
                                )
                        ),
                        new Pair<>(StateMachine.State.GRABW,
                                new ParallelGroup(
                                        ee.setEE(Consts.E_CLAW_CLOSE, Consts.BIG_WALL, Consts.LITTLE_WALL),
                                        intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
                                )
                        ),
                        new Pair<>(StateMachine.State.SPECIMENSCORE,
                                new ParallelGroup(
                                        ee.setEE(Consts.E_CLAW_CLOSE, Consts.BIG_SPECIMEN, Consts.LITTLE_SPECIMEN),
                                        intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
                                )
                        )}, null
        );
    }

    public Command next() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.next();
        StateMachine.State toState = stateMachine.getCurrentState();
        return transition(fromState, toState);
    }

    public Command previous() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.previous();
        StateMachine.State toState = stateMachine.getCurrentState();
        return transition(fromState, toState);
    }

    public Command switchToSpecimen() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.switchToSpecimen();
        StateMachine.State toState = stateMachine.getCurrentState();
        return transition(fromState, toState);
    }

    public Command switchToSample() {
        StateMachine.State fromState = stateMachine.getCurrentState();
        stateMachine.switchToSample();
        StateMachine.State toState = stateMachine.getCurrentState();
        return transition(fromState, toState);
    }

    public Command startAuto() {
        stateMachine.startAuto();
        return new ParallelGroup(
                ee.setEE(Consts.E_CLAW_CLOSE, Consts.BIG_TRANSFER, Consts.LITTLE_TRANSFER),
                intake.setIntake(Consts.I_CLAW_OPEN, Consts.PIVOT_TRANSFER, Intake.WristState.NORTH)
        );
    }

    public void flush() {
        stateMachine.flush();
    }
}
