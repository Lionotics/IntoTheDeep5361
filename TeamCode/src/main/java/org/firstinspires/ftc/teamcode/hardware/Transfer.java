package org.firstinspires.ftc.teamcode.hardware;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Transfer {

    public States currentState = States.START;
    public EndEffector ee = new EndEffector();
    public Intake intake = new Intake();

    public void init(HardwareMap hwMap) {
       intake.init(hwMap);
       ee.init(hwMap);
    }

    public enum States {
        START, BARRIER1, HOVER, GRAB, BARRIER2, HARVEST,
    }
}
