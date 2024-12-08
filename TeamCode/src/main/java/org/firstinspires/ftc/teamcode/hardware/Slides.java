package org.firstinspires.ftc.teamcode.hardware;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.helpers.PIDController;

// This class controls the slides on the robot

// There are two slides: the vertical slides and the horizontal slides
// Both are controlled by the same two motors: differentialRight and differentialLeft

// The horizontal slides are always controlled directly by the driver

// The vertical slides are sometimes controlled directly by the driver and sometimes indirectly
// The vertical slides can be in one of five states: AUTO_MOVE, MANUAL_UP, MANUAL_DOWN, HOLDING, and FREE_FALL
// AUTO_MOVE is when the slides are moving to a target position using a PID controller
// The PID controller is a class that takes in a target position and the current position and outputs a power to the motors (this power will move the slides to the target position)
// MANUAL_UP and MANUAL_DOWN are when the slides are moving up or down respectively
// HOLDING is when the slides are not moving and are just holding their position
// FREE_FALL is when we don't want to hold the slides and just let them fall (for example, when we are moving horizontal slides)
// FREE_FALL is an ironic name; it should only be used when the vertical slides are all the way down

@Config
public class Slides {

    public static final double MAX_SLIDE_UP_SPEED = 1;
    public static final double MAX_SLIDE_DOWN_SPEED = .5;
    public static final double MAX_SLIDE_HEIGHT = 2225;
    // PID constants kP, kI, and kD
    public static double kP = 0.001;
    public static double kI = 0.55;
    public static double kD = 0;
    public static int THRESHOLD = 50; // If the slides are within this threshold of the target position, they are considered to be at the target position
    public static double HOLD_POWER = 0.1; // The power needed to not move, but still counteract gravity
    private DcMotor differentialRight, differentialLeft; // The two motors that control the slides
    private PIDController controller; // The PID controller for the slides
    public static LiftState liftState = LiftState.HOLDING; // The current state of the slides

    // Initializes the hardware and sets the PID controller
    public void init(@NonNull HardwareMap hwMap) {
        differentialRight = hwMap.dcMotor.get("differentialRight");
        differentialLeft = hwMap.dcMotor.get("differentialLeft");

        differentialLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        differentialRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        differentialRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        differentialLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        differentialRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        differentialLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        controller = new PIDController(kP, kI, kD);
        controller.setTolerance(THRESHOLD);
    }

    // Setter for the vertical slides
    // Private because no other class should be setting the power of the vertical slides directly
    public void verticalSlide(double power) { //TODO: Make this private
        differentialRight.setPower(-power);
        differentialLeft.setPower(power);
    }

    // Setter for the horizontal slides
    // Public because we should should be setting the power of the vertical slides directly
    public void horizontalSlide(double power) {
        setLiftState(LiftState.FREE_FALL);
        differentialRight.setPower(power);
        differentialLeft.setPower(power);
    }

    // A method that gets run each cycle in the opmode
    // It runs the appropriate method based on the liftState
    public void loop() {
        switch (liftState) {
            case AUTO_MOVE:
                pidLoop();
                break;
            case MANUAL_DOWN:
                slideDown();
                break;
            case MANUAL_UP:
                slideUp();
                break;
            case HOLDING:
                verticalHold();
                break;
            case FREE_FALL:
                // Do nothing
                break;
        }
    }

    // Sets a target position for the vertical slides to move to
    // Public because we should be able to move the slides to a target position from other classes
    public void moveToPosition(LiftPositions position) {
        controller.setSetPoint(position.pos); // Set the target position for the PID controller
        setLiftState(LiftState.AUTO_MOVE);
    }

    // Setters for the liftState
    // These are the methods that the teleop will call to move the slides
    // Notice there is no method to move the slides to AUTO_MOVE state
    // This is because the moveToPosition method should be used to move the slides to a target position which will set the liftState to AUTO_MOVE
    public void manualUp() {
        setLiftState(LiftState.MANUAL_UP);
    }

    public void manualDown() {
        setLiftState(LiftState.MANUAL_DOWN);
    }

    public void hold() {
        if (liftState != LiftState.AUTO_MOVE || controller.atSetPoint()) {
            setLiftState(LiftState.HOLDING);
        }
    }

    // Set the power for the vertical slides
    // Private because this should only be called by loop method
    private void slideDown() {
        verticalSlide(-MAX_SLIDE_DOWN_SPEED);
    }

    private void slideUp() {
        if (getVerticalPos() < MAX_SLIDE_HEIGHT - THRESHOLD) {
            verticalSlide(MAX_SLIDE_UP_SPEED);
        }
    }

    // The method that gets run each cycle in the opmode (while the slides are in AUTO_MOVE state)
    private void pidLoop() {
        controller.setPID(kP, kI, kD);
        controller.setTolerance(THRESHOLD);
        double pow = getPidPower();
        verticalSlide(pow);
    }

    // Sets a small power to the slides to counteract gravity
    // Private because this should only be called by loop method
    private void verticalHold() {
        differentialRight.setPower(-HOLD_POWER);
        differentialLeft.setPower(HOLD_POWER);
    }
    // Getters for slide positions
    // Uses the differential formula:
    // horizontal slides position = left motor position + right motor position
    // vertical slides position = left motor position - right motor position
    public double getHorizontalPos() {
        return differentialLeft.getCurrentPosition() + differentialRight.getCurrentPosition();
    }

    public double getVerticalPos() {
        return differentialLeft.getCurrentPosition() - differentialRight.getCurrentPosition();
    }

    public double getVerticalVelo() {
        DcMotorEx left = (DcMotorEx) differentialLeft;
        DcMotorEx right = (DcMotorEx) differentialRight;
        return left.getVelocity() - right.getVelocity();
    }

    public double getTargetPos() {
        return controller.getSetPoint();
    }

    public LiftState getLiftState() {
        return liftState;
    }

    // Setter for the liftState
    // Private because no other class should be setting the liftState directly
    private void setLiftState(LiftState state) {
        liftState = state;
    }

    public double getPidPower() {
        double position = getVerticalPos();
        double pow = controller.calculate(position);
        pow = (pow < 0) ? pow * .5 : pow;
        return pow;
    }

    public Action slidesMoveTo(LiftPositions position) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    moveToPosition(position);
                    initialized = true;
                }
                loop();
                packet.put("Distance Left: ", controller.getPositionError());
                return !controller.atSetPoint();
            }
        };
    }
    public Action slidesHold(){
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                setLiftState(LiftState.HOLDING);
                loop();
                return false;
            }
        };
    }

    // Possible states for the vertical slides
    public enum LiftState {
        AUTO_MOVE, MANUAL_UP, MANUAL_DOWN, HOLDING, FREE_FALL
    }

    // Position constants for the slides to move to (in encoder ticks)
    public enum LiftPositions {
        TOP_BUCKET(2300), BOTTOM_BUCKET(900), TOP_BAR(340), TOP_HANG(0), BOTTOM(0);

        public final int pos;

        LiftPositions(int pos) {
            this.pos = pos;
        }
    }
}
