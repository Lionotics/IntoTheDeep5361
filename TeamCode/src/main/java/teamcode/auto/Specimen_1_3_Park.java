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
import teamcode.hardware.Robot;
import teamcode.hardware.StateMachine;
import teamcode.hardware.VSlides;

@Autonomous(name = "Specimen 1 + 3 W/ Park", group = "Red")
public class Specimen_1_3_Park extends OpMode {
    private static final Object lock = new Object();
    public static Pose startPose = new Pose(133, 88, Math.toRadians(0));
    public static Pose bar = new Pose(106.5, 62.2, Math.toRadians(0));
    public static Pose botSample = new Pose(111, 121, Math.toRadians(180));
    public static Pose topSample = new Pose(111, 132, Math.toRadians(180));
    public static Pose drop = new Pose(129,132, Math.toRadians(0));
    public static Pose prePickup = new Pose(106,110, Math.toRadians(180));
    public static Pose pickup = new Pose(135,110, Math.toRadians(180));
    public static Pose park = new Pose(135.5,132, Math.toRadians(0));
    private static int pathState = 0;
    public Robot robot = Robot.getInstance();
    private int iteration = 1; private final int MAX_ITERATION = 4;
    private Follower follower;
    private PathChain preload2bar, bar2bot, bot2drop, drop2top, top2drop, drop2prePickup,
            bar2prePickup, prePickup2pickup, pickup2bar, bar2park, park2start;
    private final Thread pickUpBlock = new Thread() {
        @Override
        public void run() {
            try {
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                robot.transfer.switchToSample();
                Thread.sleep(500);
                Log.d("Teamcode", "pub 1 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                Log.d("Teamcode", "pub 2 - Expected: HOVERG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(1000);
                robot.transfer.next();
                Log.d("Teamcode", "pub 3 - Expected: GRABG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(750);
                Log.i("Teamcode", "pickUpBlock finished");
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    }, placeOnBar = new Thread() {
        @Override
        public void run() {
            try {
                Log.d("Teamcode", "pob 1 - Expected: GRABW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_CHAMBER);
                Log.d("Teamcode", "pob 2 - Expected: SPECIMENSCORE, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(1500);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
                robot.transfer.next();
                Log.d("Teamcode", "pob 3 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Log.i("Teamcode", "placeOnBar finished");
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    }, waitThreeSeconds = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                Log.i("Teamcode", "waitThreeSeconds finished");
                synchronized (lock) {
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    }, pickOffWall = new Thread() {
        @Override
        public void run() {
            try {
                Log.d("Teamcode", "pow 1 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.transfer.next();
                Log.d("Teamcode", "pow 2 - Expected: GRABW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(250);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
                // Enough to lift it off the wall
                Thread.sleep(500);
                Log.i("Teamcode", "waitThreeSeconds finished");
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
        // * pickUpBlock takes you to GRABG
        // * pickOffWall takes you from HOVERW to GRABW
        // * placeOnBar takes you from GRABW to HOVERW
        preload2bar = follower.pathBuilder()
                .addPath(new Path(new BezierLine(new Point(startPose), new Point(bar))))
                .addParametricCallback(0, () -> {
                    placeOnBar.start();
                })
                .setLinearHeadingInterpolation(startPose.getHeading(), bar.getHeading())
                .build();

        bar2bot = follower.pathBuilder()
                .addPath(new BezierLine(new Point(bar), new Point(botSample)))
                .addParametricCallback(0, () -> {
                    pickUpBlock.start();
                })
                .setLinearHeadingInterpolation(bar.getHeading(), botSample.getHeading())
                .build();

        bot2drop = follower.pathBuilder()
                .addPath(new BezierLine(new Point(botSample), new Point(drop)))
                .addParametricCallback(1, () -> {
                    Log.d("Teamcode", "b2d 1 - Expected: GRABG, Actual: " + robot.transfer.stateMachine.getCurrentState());
                    robot.transfer.switchToSpecimen(); // Go to HOVERW, drop the block
                    Log.d("Teamcode", "b2d 2 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                })
                .setLinearHeadingInterpolation(botSample.getHeading(), drop.getHeading())
                .build();

        drop2top = follower.pathBuilder()
                .addPath(new BezierLine(new Point(drop), new Point(topSample)))
                .addParametricCallback(0, () -> {
                    pickUpBlock.start();
                })
                .setLinearHeadingInterpolation(drop.getHeading(), topSample.getHeading())
                .build();

        top2drop = follower.pathBuilder()
                .addPath(new BezierLine(new Point(topSample), new Point(drop)))
                .addParametricCallback(0, () -> {
                    Log.d("Teamcode", "t2d 1 - Expected: GRABG, Actual: " + robot.transfer.stateMachine.getCurrentState());
                    robot.transfer.switchToSpecimen(); // Go to HOVERW, drop the block
                    Log.d("Teamcode", "t2d 2 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                })
                .setLinearHeadingInterpolation(topSample.getHeading(), drop.getHeading())
                .build();

        drop2prePickup = follower.pathBuilder()
                .addPath(new BezierLine(new Point(drop), new Point(prePickup)))
                .addParametricCallback(1, () -> {
                    waitThreeSeconds.start();
                })
                .setLinearHeadingInterpolation(drop.getHeading(), prePickup.getHeading())
                .build();

        bar2prePickup = follower.pathBuilder()
                .addPath(new BezierLine(new Point(bar), new Point(prePickup)))
                .addParametricCallback(1, () -> {
                    waitThreeSeconds.start();
                })
                .setLinearHeadingInterpolation(bar.getHeading(), prePickup.getHeading())
                .build();

        prePickup2pickup = follower.pathBuilder()
                .addPath(new BezierLine(new Point(prePickup), new Point(pickup)))
                .addParametricCallback(1, () -> {
                    pickOffWall.start();
                })
                .setLinearHeadingInterpolation(prePickup.getHeading(), pickup.getHeading())
                .build();

        pickup2bar = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(pickup), new Point(bar)))
                .addParametricCallback(1, () -> {
                    placeOnBar.start();
                })
                .setLinearHeadingInterpolation(pickup.getHeading(), bar.getHeading())
                .build();

        bar2park = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(bar),new Point(park)))
                .setLinearHeadingInterpolation(bar.getHeading(), park.getHeading())
                .build();

        park2start = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(park), new Point(startPose)))
                .setLinearHeadingInterpolation(park.getHeading(), startPose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(preload2bar);
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
                    follower.followPath(bar2bot, true);
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
                    follower.followPath(bot2drop, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(drop2top, true);
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
                    follower.followPath(top2drop, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    follower.followPath(drop2prePickup, true);
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
                    follower.followPath(prePickup2pickup, true);
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
                    follower.followPath(pickup2bar, true);
                    setPathState(8);
                }
            case 8:
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
                    if (iteration < MAX_ITERATION) {
                        iteration++;
                        follower.followPath(bar2prePickup);
                        setPathState(6);
                    } else {
                        follower.followPath(bar2park, true);
                        setPathState(9);
                    }
                }
            case 9:
                if (!follower.isBusy()) {
                    follower.followPath(park2start);
                    setPathState(10);
                }
            case 10:
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
        //robot.vSlides.loop();
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

        robot.transfer.switchToSpecimen();
        robot.transfer.next();
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
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void stop() {
    }
}
