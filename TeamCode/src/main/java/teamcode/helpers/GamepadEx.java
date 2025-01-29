package teamcode.helpers;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadEx {

    public Button a = new Button();
    public Button b = new Button();
    public Button x = new Button();
    public Button y = new Button();

    public Button leftBumper = new Button();
    public Button rightBumper = new Button();

    public Button dpad_up = new Button();
    public Button dpad_down = new Button();
    public Button dpad_left = new Button();
    public Button dpad_right = new Button();

    public float left_stick_x;
    public float left_stick_y;
    public float right_stick_x;
    public float right_stick_y;

    public float left_trigger;
    public float right_trigger;

    public Button start = new Button();
    public Button back = new Button();


    public GamepadEx(){}

    public void update(Gamepad updatedGamepad){
        this.a.update(updatedGamepad.a);
        this.b.update(updatedGamepad.b);
        this.x.update(updatedGamepad.x);
        this.y.update(updatedGamepad.y);
        this.rightBumper.update(updatedGamepad.right_bumper);
        this.leftBumper.update(updatedGamepad.left_bumper);
        this.dpad_up.update(updatedGamepad.dpad_up);
        this.dpad_down.update(updatedGamepad.dpad_down);
        this.dpad_left.update(updatedGamepad.dpad_left);
        this.dpad_right.update(updatedGamepad.dpad_right);
        this.left_stick_x = updatedGamepad.left_stick_x;
        this.left_stick_y = updatedGamepad.left_stick_y;
        this.right_stick_x = updatedGamepad.right_stick_x;
        this.right_stick_y = updatedGamepad.right_stick_y;
        this.left_trigger = updatedGamepad.left_trigger;
        this.right_trigger = updatedGamepad.right_trigger;
        this.start.update(updatedGamepad.start);
        this.back.update(updatedGamepad.back);
    }
}