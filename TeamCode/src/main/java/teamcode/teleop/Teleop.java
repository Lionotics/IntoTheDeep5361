package teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.ftc.NextFTCOpMode;
import com.rowanmcalpin.nextftc.ftc.gamepad.GamepadEx;

import java.util.List;

import teamcode.hardware.Consts;
import teamcode.hardware.DriveTrain;
import teamcode.hardware.HSlides;
import teamcode.hardware.StateMachine;
import teamcode.hardware.Transfer;
import teamcode.hardware.VSlides;

@Config
@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends NextFTCOpMode {

    public Command driverControlled;

    public Teleop() {
        super(Transfer.INSTANCE, VSlides.INSTANCE, HSlides.INSTANCE, DriveTrain.INSTANCE);
    }

    @Override
    public void onStartButtonPressed() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Transfer.INSTANCE.flush();

        driverControlled = DriveTrain.INSTANCE.Drive(gamepadManager.getGamepad1(), true);
        driverControlled.invoke();
        GamepadEx gp1 = gamepadManager.getGamepad1();
        GamepadEx gp2 = gamepadManager.getGamepad2();

        gp1.getRightBumper().setPressedCommand(Transfer.INSTANCE::next);
        gp1.getLeftBumper().setPressedCommand(Transfer.INSTANCE::previous);

        // The triggers pass floats to the command, so we need to use lambdas
        gp1.getRightTrigger().setPressedCommand(value -> Transfer.INSTANCE.switchToSpecimen());
        gp1.getLeftTrigger().setPressedCommand(value -> Transfer.INSTANCE.switchToSample());

        gp1.getX().setPressedCommand(Transfer.INSTANCE.intake::turnWristManualLeft);
        gp1.getB().setPressedCommand(Transfer.INSTANCE.intake::turnWristManualRight);

        gp1.getA().setPressedCommand(() -> Transfer.INSTANCE.intake.setClaw(Consts.I_CLAW_OPEN));


        gp1.getDpadUp().setHeldCommand(() -> VSlides.INSTANCE.setPower(1));
        gp1.getDpadDown().setHeldCommand(() -> VSlides.INSTANCE.setPower(-1));

        gp2.getRightBumper().setPressedCommand(VSlides.INSTANCE::moveToTopBucket);
        gp2.getLeftBumper().setPressedCommand(VSlides.INSTANCE::moveToTopChamber);
        gp2.getB().setPressedCommand(VSlides.INSTANCE::moveToBottomBucket);
        gp2.getY().setPressedCommand(VSlides.INSTANCE::moveToBottomChamber);
        gp2.getA().setPressedCommand(VSlides.INSTANCE::moveToBottom);

        gp1.getDpadRight().setPressedCommand(HSlides.INSTANCE::slideOut);
        gp1.getDpadLeft().setPressedCommand(HSlides.INSTANCE::slideIn);

        gp2.getDpadRight().setPressedCommand(HSlides.INSTANCE::slideOut);
        gp2.getDpadLeft().setPressedCommand(HSlides.INSTANCE::slideIn);
    }

    @Override
    public void onUpdate() {
        StateMachine.State state = Transfer.INSTANCE.stateMachine.getCurrentState();
        List<StateMachine.State> line = Transfer.INSTANCE.stateMachine.getCurrentLine();
        StateMachine.State lineCap = line.get(line.size() - 1);

        telemetry.addData("State", state.name());
        telemetry.addData("Linecap", lineCap.name());
        telemetry.addData("Wrist", Transfer.INSTANCE.intake.currentWristState.name());
        telemetry.addData("Slides", VSlides.INSTANCE.getPos());
        telemetry.update();
    }

}