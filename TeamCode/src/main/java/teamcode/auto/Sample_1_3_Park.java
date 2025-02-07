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
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Arrays;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.EndEffector;
import teamcode.hardware.Intake;
import teamcode.hardware.Robot;
import teamcode.hardware.StateMachine;
import teamcode.hardware.VSlides;

@Autonomous(name = "Sample 1 + 3 W/ Park", group = "Red")
public class Sample_1_3_Park extends OpMode {
    private static final Object lock = new Object();
    public static Pose startPose = new Pose(133, 36, Math.toRadians(90));
    public static Pose basket = new Pose(128.2946175637394, 7.546742209631725, Math.toRadians(135));
    public static Pose topSample = new Pose(113, 13.5, Math.toRadians(180));
    public static Pose midSample = new Pose(113.5, 3.5, Math.toRadians(180));
    public static Pose botSample = new Pose(115.75, 4, Math.toRadians(230));
    public static Pose park1 = new Pose(83,12, Math.toRadians(90));
    public static Pose park2 = new Pose(83,45, Math.toRadians(90));
    private static int pathState = 0;
    public Robot robot = Robot.getInstance();
    private Follower follower;
    private PathChain preload2basket, basket2top, top2basket, basket2mid, mid2basket, basket2bot,
            bot2basket, basket2park, park2start;
    private final Thread placeInBucket = new Thread() {
        @Override
        public void run() {
            try {
                Log.d("Teamcode", "pib 1 - Expected: TRANSFER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                Thread.sleep(1000);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
                robot.vSlides.loop();
                Thread.sleep(1500);
                robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                Thread.sleep(1500);
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
                    robot.transfer.intake.turnWristManualRight();
                    Thread.sleep(1000);
                    robot.transfer.intake.setClaw(Intake.IntakeConsts.CLAW_CLOSE);
                    Thread.sleep(500);
                }
                Log.d("Teamcode", "pub 2 - Expected: HOVERG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.transfer.next();
                Log.d("Teamcode", "pub 3 - Expected: GRABG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(750);
                robot.transfer.next();
                Log.d("Teamcode", "pub 4 - Expected: TRANSFER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(1250);
                Log.i("Teamcode", "pickUpBlock finished");
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    };

    public void buildPaths() {
        // NOTES:
        // * pickUpBlock takes you from BARRIER to TRANSFER
        // * putInBasket takes you from TRANSFER to BARRIER
        preload2basket = follower.pathBuilder()
                .addPath(new Path(new BezierLine(new Point(startPose), new Point(basket))))
                .addParametricCallback(0, () -> {
                    Log.d("Teamcode", "p2b started");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                robot.transfer.ee.setBigPivot(EndEffector.EEConsts.BIG_SAMPLE);
                                robot.transfer.ee.setLittlePivot(EndEffector.EEConsts.LITTLE_SAMPLE);
                                robot.transfer.intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                                robot.transfer.intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                                Log.d("Teamcode", "p2b in progress...");
                                Thread.sleep(2000);
                                robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
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
                .setLinearHeadingInterpolation(startPose.getHeading(), basket.getHeading())
                .build();

        basket2top = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(topSample)))
                .addParametricCallback(0, () -> {
                    Log.d("Teamcode", "b2t 1 - Expected: START; Actual: " + robot.transfer.stateMachine.getCurrentState());
                    robot.transfer.next(); // Move from START to BARRIER
                    Log.d("Teamcode", "b2t 2 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                    pickUpBlock.start(); // Go to TRANSFER
                })
                .setLinearHeadingInterpolation(basket.getHeading(), topSample.getHeading())
                .build();

        top2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(topSample), new Point(basket)))
                .addParametricCallback(0, placeInBucket::start)
                .setLinearHeadingInterpolation(topSample.getHeading(), basket.getHeading())
                .build();

        basket2mid = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(midSample)))
                .addParametricCallback(0, pickUpBlock::start)
                .setLinearHeadingInterpolation(basket.getHeading(), midSample.getHeading())
                .build();

        mid2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midSample), new Point(basket)))
                .addParametricCallback(0, placeInBucket::start)
                .setLinearHeadingInterpolation(midSample.getHeading(), basket.getHeading())
                .build();

        basket2bot = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(botSample)))
                .addParametricCallback(0, pickUpBlock::start)
                .setLinearHeadingInterpolation(basket.getHeading(), botSample.getHeading())
                .build();

        bot2basket = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botSample), new Point(midSample)))
                .setLinearHeadingInterpolation(botSample.getHeading(), midSample.getHeading())
                .addPath(new BezierLine(new Point(midSample), new Point(basket)))
                .addParametricCallback(0, placeInBucket::start)
                .setLinearHeadingInterpolation(midSample.getHeading(), basket.getHeading())
                .build();

        basket2park = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(basket), new Point(park1)))
                .addParametricCallback(0, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                    robot.vSlides.loop();
                })
                .setLinearHeadingInterpolation(basket.getHeading(), park1.getHeading())
                .addPath(new BezierCurve(new Point(park1), new Point(park2)))
                .setLinearHeadingInterpolation(park1.getHeading(), park2.getHeading())
                .build();

        park2start = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(park2), new Point(park1)))
                .setLinearHeadingInterpolation(park2.getHeading(), park1.getHeading())
                .addPath(new BezierCurve(new Point(park1), new Point(startPose)))
                .setLinearHeadingInterpolation(park1.getHeading(), startPose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(preload2basket);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(basket2top, true);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(top2basket, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(basket2mid, true);
                    setPathState(4);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(mid2basket, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(basket2bot, true);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(bot2basket, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (!follower.isBusy()) {
                    try {
                        synchronized (lock) {
                            Log.i("Teamcode", "Main thread locking...");
                            lock.wait();
                            Log.i("Teamcode", "Main thread notified");
                        }
                    } catch (InterruptedException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(basket2park, true);
                    setPathState(8);
                }
            case 8:
                if (!follower.isBusy()) {
                    follower.followPath(park2start, true);
                    setPathState(9);
                }
            case 9:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                    setPathState(-1);
                }
                break;
        }
    }


    public void setPathState(int pState) {
        pathState = pState;
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void loop() {

        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

        StateMachine.State state = robot.transfer.stateMachine.getCurrentState();

        robot.hSlides.hold();
        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
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
    public void init() {
        robot.init(hardwareMap);
        robot.transfer.flush();
        Log.d("Teamcode", "Entered init callback");
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();

        robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_CLOSE);
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void start() {
        robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
        robot.vSlides.loop();
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void stop() {
        synchronized (lock) {
            lock.notify();
        }
    }

}


