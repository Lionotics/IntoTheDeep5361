package org.firstinspires.ftc.teamcode.teleop;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.ScheduleCommand;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.SlidesSubsystem;

@TeleOp(name = "CommandTeleop", group = "Teleop")
public class CommandTeleop extends CommandOpMode {
    DriveSubsystem drive;
    SlidesSubsystem slides;

    @Override
    public void initialize() {
        drive = new DriveSubsystem(hardwareMap);
        slides = new SlidesSubsystem(hardwareMap);

        schedule(new );
    }
}
