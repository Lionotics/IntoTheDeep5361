package org.firstinspires.ftc.teamcode.helpers;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadEx {

    public Button a = new Button();
    public Button b = new Button();
    public Button x = new Button();
    public Button y = new Button();
    public Button up = new Button();
    public Button down = new Button();
    public Button left = new Button();
    public Button right = new Button();
    public Button leftBumper = new Button();
    public Button rightBumper = new Button();

    public GamepadEx(){}

    public void update(Gamepad updatedGamepad){
        this.a.update(updatedGamepad.a);
        this.b.update(updatedGamepad.b);
        this.x.update(updatedGamepad.x);
        this.y.update(updatedGamepad.y);
        this.up.update(updatedGamepad.dpad_up);
        this.down.update(updatedGamepad.dpad_down);
        this.left.update(updatedGamepad.dpad_left);
        this.right.update(updatedGamepad.dpad_right);
        this.rightBumper.update(updatedGamepad.right_bumper);
        this.leftBumper.update(updatedGamepad.left_bumper);
    }
}