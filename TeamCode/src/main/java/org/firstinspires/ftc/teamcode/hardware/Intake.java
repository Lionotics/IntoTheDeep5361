package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Intake {

    public static double CLAW_START = 0.49, CLAW_BARRIER_1 = 0.49, CLAW_HOVER = 0.49,
                CLAW_GRAB = 0.385, CLAW_BARRIER_2 = 0.385 , CLAW_HARVEST = 0.385;
    public static double WRIST_START = 0.685, WRIST_BARRIER_1 = 0.685, WRIST_HOVER = 0.685,
                WRIST_BARRIER_2 = 0.685, WRIST_HARVEST = 0.685;
    public static double ELBOW_START = 0.4, ELBOW_BARRIER_1 = 0.7, ELBOW_HOVER = 0.975,
                ELBOW_GRAB = 0.975, ELBOW_BARRIER_2 = 0.7, ELBOW_HARVEST = 0.4;
    public static double SHOULDER_START = 0.75, SHOULDER_BARRIER_1 = 0.48, SHOULDER_HOVER = 0.435,
                SHOULDER_GRAB = 0.435, SHOULDER_BARRIER_2 = 0.48, SHOULDER_HARVEST = 0.75;

    public enum IntakeState {
        START, BARRIER1, HOVER, GRAB, BARRIER2, HARVEST
    }
    public static enum WristState {
        LEFT(0.0), STRAIGHT(0.685), RIGHT(1.0);

        public final double pos;
        WristState(double pos) {
            this.pos = pos;
        }
    }

    Servo claw, wrist, elbow, shoulder;
    EndEffector ee = new EndEffector();

    public void init(HardwareMap hwMap) {
        claw = hwMap.servo.get("intakeClaw");
        wrist = hwMap.servo.get("clawRotate");
        elbow = hwMap.servo.get("intakeRotate");
        shoulder = hwMap.servo.get("intakePivot");

        ee.init(hwMap);
    }

    public void setClaw(double position) {
        claw.setPosition(position);
    }

    public double getClawPosition() {
        return claw.getPosition();
    }

    public void setWrist(double position) {
        wrist.setPosition(position);
    }

    public double getWristPosition() {
        return wrist.getPosition();
    }

    public void setElbow(double position) {
        elbow.setPosition(position);
    }

    public double getElbowPosition() {
        return elbow.getPosition();
    }

    public void setShoulder(double position) {
        shoulder.setPosition(position);
    }

    public double getShoulderPosition() {
        return shoulder.getPosition();
    }

    public IntakeState currentState;

    public void init() {
        setClaw(CLAW_START);
        setWrist(WRIST_START);
        setElbow(ELBOW_START);
        setShoulder(SHOULDER_START);
        currentState = IntakeState.START;
    }

    private void startToBarrier1() {
        setClaw(CLAW_BARRIER_1);
        setWrist(WRIST_BARRIER_1);
        setElbow(ELBOW_BARRIER_1);
        setShoulder(SHOULDER_BARRIER_1);
        currentState = IntakeState.BARRIER1;
    }

    private void barrier1ToHover() {
        setClaw(CLAW_HOVER);
        setWrist(WRIST_HOVER);
        setElbow(ELBOW_HOVER);
        setShoulder(SHOULDER_HOVER);
        currentState = IntakeState.HOVER;
    }

    private void hoverToGrab() {
        setClaw(CLAW_GRAB);
        setElbow(ELBOW_GRAB);
        setShoulder(SHOULDER_GRAB);
        currentState = IntakeState.GRAB;
    }

    private void grabToBarrier2() {
        setClaw(CLAW_BARRIER_2);
        setWrist(WRIST_BARRIER_2);
        setElbow(ELBOW_BARRIER_2);
        setShoulder(SHOULDER_BARRIER_2);
        ee.open();
        currentState = IntakeState.BARRIER2;
    }

    private void barrier2ToHarvest(LinearOpMode lom) {
        ee.rotateDown();
        setClaw(CLAW_HARVEST);
        setWrist(WRIST_HARVEST);
        setElbow(ELBOW_HARVEST);
        setShoulder(SHOULDER_HARVEST);
        lom.sleep(1000);
        ee.close();
        currentState = IntakeState.HARVEST;
    }

    private void harvestToStart(LinearOpMode lom) {
        setClaw(CLAW_START);
        setWrist(WRIST_START);
        setElbow(ELBOW_START);
        setShoulder(SHOULDER_START);
        ee.rotateUp();
        currentState = IntakeState.START;
    }

    private void startToHarvest() {
        ee.rotateDown();
        ee.close();
        setClaw(CLAW_HARVEST);
        setWrist(WRIST_HARVEST);
        setElbow(ELBOW_HARVEST);
        setShoulder(SHOULDER_HARVEST);
        currentState = IntakeState.HARVEST;
    }

    private void harvestToBarrier2() {
        ee.open();
        ee.rotateUp();
        setClaw(CLAW_BARRIER_2);
        setWrist(WRIST_BARRIER_2);
        setElbow(ELBOW_BARRIER_2);
        setShoulder(SHOULDER_BARRIER_2);
        currentState = IntakeState.BARRIER2;
    }

    private void barrier2ToGrab() {
        setClaw(CLAW_GRAB);
        setElbow(ELBOW_GRAB);
        setShoulder(SHOULDER_GRAB);
        ee.close();
        currentState = IntakeState.GRAB;
    }

    private void grabToHover() {
        setClaw(CLAW_HOVER);
        setElbow(ELBOW_HOVER);
        setShoulder(SHOULDER_HOVER);
        currentState = IntakeState.HOVER;
    }

    private void hoverToBarrier1() {
        setClaw(CLAW_BARRIER_1);
        setWrist(WRIST_BARRIER_1);
        setElbow(ELBOW_BARRIER_1);
        setShoulder(SHOULDER_BARRIER_1);
        currentState = IntakeState.BARRIER1;
    }

    private void barrier1ToStart() {
        setClaw(CLAW_START);
        setWrist(WRIST_START);
        setElbow(ELBOW_START);
        setShoulder(SHOULDER_START);
        currentState = IntakeState.START;
    }

    public void incrementState(LinearOpMode lom) {
        switch (currentState) {
            case START:
                startToBarrier1();
                break;
            case BARRIER1:
                barrier1ToHover();
                break;
            case HOVER:
                hoverToGrab();
                break;
            case GRAB:
                grabToBarrier2();
                break;
            case BARRIER2:
                barrier2ToHarvest(lom);
                break;
            case HARVEST:
                harvestToStart(lom);
                break;
        }
    }

    public void decrementState() {
        switch (currentState) {
            case START:
                startToHarvest();
                break;
            case BARRIER1:
                barrier1ToStart();
                break;
            case HOVER:
                hoverToBarrier1();
                break;
            case GRAB:
                grabToHover();
                break;
            case BARRIER2:
                barrier2ToGrab();
                break;
            case HARVEST:
                harvestToBarrier2();
                break;
        }
    }

    private WristState currentWristState = WristState.STRAIGHT;

    public void turnWristRight() {
        if (currentState != IntakeState.HOVER && currentState != IntakeState.GRAB) return;
        switch (currentWristState) {
            case LEFT:
                setWrist(WristState.STRAIGHT.pos);
                currentWristState = WristState.STRAIGHT;
                break;
            case STRAIGHT:
                setWrist(WristState.RIGHT.pos);
                currentWristState = WristState.RIGHT;
                break;
            default:
                break;
        }
    }

    public void turnWristLeft() {
        if (currentState != IntakeState.HOVER && currentState != IntakeState.GRAB) return;
        switch (currentWristState) {
            case RIGHT:
                setWrist(WristState.STRAIGHT.pos);
                currentWristState = WristState.STRAIGHT;
                break;
            case STRAIGHT:
                setWrist(WristState.LEFT.pos);
                currentWristState = WristState.LEFT;
                break;
            default:
                break;
        }
    }
}
