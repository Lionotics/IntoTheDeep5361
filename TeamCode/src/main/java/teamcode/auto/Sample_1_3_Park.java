package teamcode.auto;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.rowanmcalpin.nextftc.core.command.Command;
import com.rowanmcalpin.nextftc.core.command.CommandManager;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.pedro.FollowPath;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import java.util.Arrays;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.Consts;
import teamcode.hardware.HSlides;
import teamcode.hardware.StateMachine;
import teamcode.hardware.Transfer;
import teamcode.hardware.VSlides;

@Autonomous(name = "Sample 1 + 3 W/ Park", group = "Red")
public class Sample_1_3_Park extends PedroOpMode {
    public static Pose startPose = new Pose(133, 36, Math.toRadians(90));
    public static Pose basket = new Pose(128.2946175637394, 7.546742209631725, Math.toRadians(135));
    public static Pose topSample = new Pose(113, 13.5, Math.toRadians(180));
    public static Pose midSample = new Pose(113.5, 3.5, Math.toRadians(180));
    public static Pose botSample = new Pose(114.5, 3, Math.toRadians(230));
    public static Pose botBackup = new Pose(120, 10, Math.toRadians(230));
    public static Pose park1 = new Pose(83, 12, Math.toRadians(90));
    public static Pose park2 = new Pose(83, 45, Math.toRadians(90));
    private final Thread placeInBucket = new Thread() {
        @Override
        public void run() {
            try {
                Log.d("Teamcode", "pib 1 - Expected: TRANSFER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                while (ee.bigMonitor.isWithinThreshold(Consts.BIG_TRANSFER)) {
                    Thread.sleep(10);
                }
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
                robot.vSlides.loop();
                Thread.sleep(1500);
                ee.setClaw(Consts.E_CLAW_OPEN);
                Thread.sleep(1000);
                Log.d("Teamcode", "pib 2 - Expected: SAMPLESCORE; Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                Log.d("Teamcode", "pib 3 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Log.i("Teamcode", "placeInBucket finished");
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    }, pickUpBlock = new Thread() {

        @Override
        public void run() {
            try {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                robot.vSlides.loop();
                Thread.sleep(1500);
                Log.d("Teamcode", "pub 1 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                if (pathState == 6) {
                    intake.turnWristManualRight();
                    Thread.sleep(500);
                    intake.setClaw(Consts.I_CLAW_CLOSE);
                    Thread.sleep(500);
                }
                Log.d("Teamcode", "pub 2 - Expected: HOVERG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.transfer.next();
                Log.d("Teamcode", "pub 3 - Expected: GRABG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(750);
                robot.transfer.next();
                Log.d("Teamcode", "pub 4 - Expected: TRANSFER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(750);
                Log.i("Teamcode", "pickUpBlock finished");
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    };
    private PathChain preload2basket, basket2top, top2basket, basket2mid, mid2basket, basket2bot,
            bot2basket, basket2park, park2start;
    public Sample_1_3_Park() {
        super(Transfer.INSTANCE, VSlides.INSTANCE, HSlides.INSTANCE);
    }

    public void buildPaths() {
        // NOTES:
        // * pickUpBlock takes you from BARRIER to TRANSFER
        // * putInBasket takes you from TRANSFER to BARRIER
        preload2basket = follower.pathBuilder()
                .addPath(new Path(new BezierLine(new Point(startPose), new Point(basket))))
                .setLinearHeadingInterpolation(startPose.getHeading(), basket.getHeading())
                .addParametricCallback(0, () -> {
                    Log.d("Teamcode", "p2b started");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                ee.setBigPivot(Consts.BIG_SAMPLE);
                                ee.setLittlePivot(Consts.LITTLE_SAMPLE);
                                intake.alignWrist();
                                intake.setPivot(Consts.PIVOT_TRANSFER);
                                Log.d("Teamcode", "p2b in progress...");
                                Thread.sleep(2000);
                                ee.setClaw(Consts.E_CLAW_OPEN);
                                Thread.sleep(1250);
                                Log.d("Teamcode", "p2b finished");
                            } catch (InterruptedException err) {
                                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                            }
                            synchronized (lock) {
                                lock.notify();
                            }
                        }
                    }.start();
                })
                .build();

        basket2top = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(topSample)))
                .setLinearHeadingInterpolation(basket.getHeading(), topSample.getHeading())
                .addParametricCallback(0, () -> {
                    Log.d("Teamcode", "b2t 1 - Expected: START; Actual: " + robot.transfer.stateMachine.getCurrentState());
                    robot.transfer.next(); // Move from START to BARRIER
                    Log.d("Teamcode", "b2t 2 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                    pickUpBlock.start(); // Go to TRANSFER
                })
                .build();

        top2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(topSample), new Point(basket)))
                .setLinearHeadingInterpolation(topSample.getHeading(), basket.getHeading())
                .addParametricCallback(0, placeInBucket::start)
                .build();

        basket2mid = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(midSample)))
                .setLinearHeadingInterpolation(basket.getHeading(), midSample.getHeading())
                .addParametricCallback(0, pickUpBlock::start)
                .build();

        mid2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midSample), new Point(basket)))
                .setLinearHeadingInterpolation(midSample.getHeading(), basket.getHeading())
                .addParametricCallback(0, placeInBucket::start)
                .build();

        basket2bot = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(botSample)))
                .setLinearHeadingInterpolation(basket.getHeading(), botSample.getHeading())
                .addParametricCallback(0, pickUpBlock::start)
                .build();

        bot2basket = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botSample), new Point(botBackup)))
                .setLinearHeadingInterpolation(botSample.getHeading(), botBackup.getHeading())
                .addPath(new BezierLine(new Point(botBackup), new Point(basket)))
                .setLinearHeadingInterpolation(botBackup.getHeading(), basket.getHeading())
                .addParametricCallback(0, placeInBucket::start)
                .build();

        basket2park = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(basket), new Point(topSample)))
                .addParametricCallback(0, () -> {
                    robot.transfer.next();
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                    robot.vSlides.loop();
                })
                .setLinearHeadingInterpolation(basket.getHeading(), topSample.getHeading())
//                .addPath(new BezierCurve(new Point(park1), new Point(park2)))
//                .setLinearHeadingInterpolation(park1.getHeading(), park2.getHeading())
                .build();

        park2start = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(park2), new Point(park1)))
                .setLinearHeadingInterpolation(park2.getHeading(), park1.getHeading())
                .addPath(new BezierCurve(new Point(park1), new Point(startPose)))
                .setLinearHeadingInterpolation(park1.getHeading(), startPose.getHeading())
                .build();
    }

    public Command routine() {
        return new SequentialGroup(
                new FollowPath(preload2basket),
                new FollowPath(basket2top, true),
                new FollowPath(top2basket, true),
                new FollowPath(basket2mid, true),
                new FollowPath(mid2basket, true),
                new FollowPath(basket2bot, true),
                new FollowPath(bot2basket, true),
                new FollowPath(basket2park, true),
                new FollowPath(park2start, true),
                );
    }

    @Override
    public void onUpdate() {
        StateMachine.State state = Transfer.INSTANCE.stateMachine.getCurrentState();
        telemetry.addData("Commands", CommandManager.INSTANCE.getRunningCommands().toString());
        telemetry.addData("State", state.name());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void onInit() {
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0, 0));
        buildPaths();
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     * Use this to collect info from the gamepad to change variables.
     **/
    @Override
    public void onWaitForStart() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void onStartButtonPressed() {
        routine().invoke();
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void onStop() {
    }

}


