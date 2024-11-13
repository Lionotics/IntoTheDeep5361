package org.firstinspires.ftc.teamcode.hardware;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;


@Config
public class Slides {

    private DcMotor differentialRight, differentialLeft;

    //TODO: Incorporate this into the class
    /*
    private DcMotor differentialRight, differentialLeft;

    @Override
    public void runOpMode() {
        differentialRight = hardwareMap.dcMotor.get("differentialRight");
        differentialLeft = hardwareMap.dcMotor.get("differentialLeft");

        differentialLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        differentialRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                differentialRight.setPower(-.7);
                differentialLeft.setPower(.7);
            } else if (gamepad1.dpad_down) {
                differentialRight.setPower(.7);
                differentialLeft.setPower(-.7);
            } else if (gamepad1.dpad_left) {
                differentialRight.setPower(-.7);
                differentialLeft.setPower(-.7);
            } else if (gamepad1.dpad_right) {
                differentialRight.setPower(.7);
                differentialLeft.setPower(.7);
            } else {
                differentialRight.setPower(-0.1);
                differentialLeft.setPower(0.1);
            }
        }*/
    double FIRST_POS = 1000;
    double SECOND_POS = 2000;
    double HOLD =.03;
    SlideState state = SlideState.BOTTOM;
    SlideState goalState = SlideState.BOTTOM;


    public void loop() {
        switch (state){
            case UP:
                slide.setPower(1);
                if(goalState == SlideState.FIRST && slide.getCurrentPosition() >= FIRST_POS){
                    state = SlideState.FIRST;
                } else if (goalState == SlideState.SECOND && slide.getCurrentPosition() >= SECOND_POS){
                    state = SlideState.SECOND;
                }
            case DOWN:
                slide.setPower(-1);
                if(goalState == SlideState.FIRST && slide.getCurrentPosition() <= FIRST_POS){
                    state = SlideState.FIRST;
                } else if (goalState == SlideState.BOTTOM && slide.getCurrentPosition() <= 0){
                    state = SlideState.BOTTOM;
                }
                break;
            default:
                slide.setPower(HOLD);
        }
    }

    public void upHandler() {
        switch(state){
            case BOTTOM:
                state = SlideState.UP;
                goalState = SlideState.FIRST;
                break;
            case FIRST:
                state = SlideState.UP;
                goalState = SlideState.SECOND;
                break;
            default:
                break;
        }
    }
    public void downHandler() {
        switch(state){
            case FIRST:
                state = SlideState.DOWN;
                goalState = SlideState.BOTTOM;
                break;
            case SECOND:
                state = SlideState.DOWN;
                goalState = SlideState.FIRST;
                break;
            default:
                break;
        }
    }

    public enum SlideState {
        UP,
        DOWN,
        BOTTOM,
        FIRST,
        SECOND
    }

    DcMotor slide;
    public void init(HardwareMap hwMap) {
        differentialRight = hwMap.dcMotor.get("differentialRight");
        differentialLeft = hwMap.dcMotor.get("differentialLeft");

        differentialLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        differentialRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
}
