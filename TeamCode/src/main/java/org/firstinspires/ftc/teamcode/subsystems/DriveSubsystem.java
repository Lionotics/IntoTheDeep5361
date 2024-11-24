package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

@Config
public class DriveSubsystem extends SubsystemBase {
    public MecanumDrive drive;
    public RevIMU imu;
    public static double MAX_SPEED = .8;

    private final IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.LEFT));

    public DriveSubsystem(HardwareMap hwMap) {
        Motor frontLeft = new Motor(hwMap, "frontLeft", Motor.GoBILDA.RPM_435);
        Motor frontRight = new Motor(hwMap, "frontRight", Motor.GoBILDA.RPM_435);
        Motor backLeft = new Motor(hwMap, "backLeft", Motor.GoBILDA.RPM_435);
        Motor backRight = new Motor(hwMap, "backRight", Motor.GoBILDA.RPM_435);

        drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);
        drive.setRightSideInverted(true);
        drive.setMaxSpeed(MAX_SPEED);

        imu = new RevIMU(hwMap);
        imu.init();
    }

}
