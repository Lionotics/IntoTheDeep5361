package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class Robot {
    public DriveTrain driveTrain = new DriveTrain();

    public void init(HardwareMap hwMap) {
        driveTrain.init(hwMap);
    }

}
