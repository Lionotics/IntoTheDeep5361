package org.firstinspires.ftc.teamcode.hardware;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;


@Config
public class HSlides {
    public static final double MAX_SPEED = 1;
    private CRServo hSlideLeft, hSlideRight; // The two servos that control the slides

    // Initializes the hardware and sets the PID controller
    public void init(@NonNull HardwareMap hwMap) {
        hSlideLeft = hwMap.get(CRServo.class, "hSlideLeft");
        hSlideRight = hwMap.get(CRServo.class, "hSlideRight");

    }

    public void slide(boolean in){
        int multiplier = in ? 1 : -1;
        hSlideLeft.setPower(-multiplier * MAX_SPEED);
        hSlideRight.setPower(multiplier * MAX_SPEED);
    }

    public void hold() {
        hSlideLeft.setPower(0);
        hSlideRight.setPower(0);
    }
}
