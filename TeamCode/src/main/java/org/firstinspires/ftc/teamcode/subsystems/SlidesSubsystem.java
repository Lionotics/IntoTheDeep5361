package org.firstinspires.ftc.teamcode.subsystems;


import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


@Config
public class SlidesSubsystem extends SubsystemBase {

    private final double HOLD_VERTICAL_SLIDE_POWER = 0.1d;
    private DcMotor differentialRight, differentialLeft;

    public SlidesSubsystem(HardwareMap hwMap) {
        differentialRight = hwMap.dcMotor.get("differentialRight");
        differentialLeft = hwMap.dcMotor.get("differentialLeft");

        differentialLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        differentialRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void verticalSlide(double power) {
        differentialRight.setPower(-power);
        differentialLeft.setPower(power);
    }

    public void horizontalSlide(double power) {
        differentialRight.setPower(power);
        differentialLeft.setPower(power);
    }

    public void hold() {
        differentialRight.setPower(-HOLD_VERTICAL_SLIDE_POWER);
        differentialLeft.setPower(HOLD_VERTICAL_SLIDE_POWER);
    }

    public void setDifferentialLeft(double power) {
        differentialLeft.setPower(power);
    }

    public void setDifferentialRight(double power) {
        differentialRight.setPower(power);
    }

    public double getHorizontalPos() {
        return differentialLeft.getCurrentPosition() + differentialRight.getCurrentPosition();
    }

    public double getVerticalPos() {
        return differentialLeft.getCurrentPosition() - differentialRight.getCurrentPosition();
    }

}
