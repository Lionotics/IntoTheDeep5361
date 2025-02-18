//package teamcode.testing;
//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import teamcode.hardware.Consts;
//import teamcode.hardware.EndEffector;
//import teamcode.helpers.GamepadEx;
//
//@TeleOp(name="End Effector Testing", group = "Testing")
//public class EndEffectorTesting extends LinearOpMode {
//    Robot robot = Robot.getInstance();
//    GamepadEx gp1 = new GamepadEx();
//    private EndEffector ee;
//
//
//    @Override
//    public void runOpMode() {
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
//        robot.init(hardwareMap);
//        ee = robot.transfer.ee;
//        waitForStart();
//
//        while (opModeIsActive()) {
//            if (gp1.y.isCurrentlyPressed()) {
//                ee.setClaw(Consts.E_CLAW_OPEN);
//            } else if (gp1.a.isCurrentlyPressed()) {
//                ee.setClaw(Consts.I_CLAW_CLOSE);
//            }
//
//            if (gp1.b.isCurrentlyPressed()) {
//                ee.setBigPivot(Consts.BIG_GRAB);
//            } else if (gp1.x.isCurrentlyPressed()) {
//                ee.setBigPivot(Consts.BIG_TRANSFER);
//            } else if (gp1.back.isCurrentlyPressed()) {
//                ee.setBigPivot(Consts.BIG_SAMPLE);
//            } else if (gp1.start.isCurrentlyPressed()) {
//                ee.setBigPivot(Consts.BIG_WALL);
//            } else if (gp1.rightBumper.isCurrentlyPressed()) {
//                ee.setBigPivot(Consts.BIG_SPECIMEN);
//            }
//
//            if (gp1.dpad_up.isCurrentlyPressed()) {
//                ee.setLittlePivot(Consts.LITTLE_TRANSFER);
//            } else if (gp1.dpad_right.isCurrentlyPressed()){
//                ee.setLittlePivot(Consts.LITTLE_WALL);
//            } else if (gp1.dpad_down.isCurrentlyPressed()){
//                ee.setLittlePivot(Consts.LITTLE_SAMPLE);
//            } else if (gp1.dpad_left.isCurrentlyPressed()){
//                ee.setLittlePivot(Consts.LITTLE_SPECIMEN);
//            }
//
//            telemetry.addData("claw",ee.getClawPos());
//            telemetry.addData("big",ee.getBigPos());
//            telemetry.addData("little",ee.getLittlePos());
//            telemetry.update();
//            gp1.update(gamepad1);
//        }
//
//    }
//}
