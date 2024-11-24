package org.firstinspires.ftc.teamcode.hardware;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
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

    public static final double MAX_SLIDE_SPEED = 1.0;
    // PID constants kP, kI, and kD
    public static double kP = 0.1;
    public static double kI = 0.1;
    public static double kD = 0.1;
    public static int THRESHOLD = 10; // If the slides are within this threshold of the target position, they are considered to be at the target position
    public static double HOLD_POWER = 0.1; // The power needed to not move, but still counteract gravity
    private DcMotor differentialRight, differentialLeft; // The two motors that control the slides
    private PIDController controller; // The PID controller for the slides
    private LiftState liftState = LiftState.HOLDING; // The current state of the slides

    // Possible states for the vertical slides
    public enum LiftState {
        AUTO_MOVE, MANUAL_UP, MANUAL_DOWN, HOLDING, FREE_FALL
    }

    // Position constants for the slides to move to (in encoder ticks)
    public enum LiftPositions {
        TOP_BUCKET(0), BOTTOM_BUCKET(0), TOP_BAR(0), BOTTOM_BAR(0), BOTTOM(0);

        public final int pos;

        LiftPositions(int pos) {
            this.pos = pos;
        }
    }

    // Initializes the hardware and sets the PID controller
    public void init(@NonNull HardwareMap hwMap) {
        differentialRight = hwMap.dcMotor.get("differentialRight");
        differentialLeft = hwMap.dcMotor.get("differentialLeft");

        differentialLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        differentialRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        controller = new PIDController(kP, kI, kD);
        controller.setTolerance(THRESHOLD);
    }

    // Setter for the vertical slides
    // Private because no other class should be setting the power of the vertical slides directly
    private void verticalSlide(double power) {
        differentialRight.setPower(-power);
        differentialLeft.setPower(power);
    }

    // Setter for the horizontal slides
    // Public because we should should be setting the power of the vertical slides directly
    public void horizontalSlide() {
        setLiftState(LiftState.FREE_FALL);
        differentialRight.setPower(-MAX_SLIDE_SPEED);
        differentialLeft.setPower(MAX_SLIDE_SPEED);
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

    // Setter for the liftState
    // Private because no other class should be setting the liftState directly
    private void setLiftState(LiftState state) {
        liftState = state;
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
        setLiftState(LiftState.HOLDING);
    }

    // Set the power for the vertical slides
    // Private because this should only be called by loop method
    private void slideDown() {
        verticalSlide(-MAX_SLIDE_SPEED);
    }

    private void slideUp() {
        verticalSlide(MAX_SLIDE_SPEED);
    }

    // The method that gets run each cycle in the opmode (while the slides are in AUTO_MOVE state)
    private void pidLoop() {
        double position = getVerticalPos();
        double power = controller.calculate(position);
        power = Math.min(Math.abs(power), MAX_SLIDE_SPEED); // Limit the power to the maximum slide speed
        verticalSlide(power);
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

}
