package teamcode.auto;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.HSlides;
import teamcode.hardware.Robot;
import teamcode.hardware.StateMachine;
import teamcode.hardware.VSlides;

@Autonomous(name = "Specimen 1 + 3 W/ Park", group = "Red")
public class Specimen_1_3_Park extends OpMode {
    private static final Object lock = new Object();
    public static Pose startPose = new Pose(135.75, 88, Math.toRadians(180));
    public static Pose botSample = new Pose(115.75, 106, Math.toRadians(140));
    public static Pose midSample = new Pose(115.75, 117, Math.toRadians(135));
    public static Pose topSample = new Pose(115.75, 126, Math.toRadians(130));
    public static Pose botDrop = new Pose(115.75, 106, Math.toRadians(55));
    public static Pose midDrop = new Pose(115.75, 117, Math.toRadians(55));
    public static Pose topDrop = new Pose(115.75, 126, Math.toRadians(55));
    public static Point topControl = new Point(115.75, 117, Point.CARTESIAN);
    private static int pathState = 0;
    public Robot robot = Robot.getInstance();
//    private int iteration = 1; private final int MAX_ITERATION = 4;
    public static int waitTime = 1000;
    private Follower follower;
    private PathChain start2bot, bot2drop, drop2mid, mid2drop, drop2top, top2drop;
    private final CompletableFuture<Void> pickUpBlock = CompletableFuture.runAsync(() -> {
        try {
            robot.transfer.switchToSample();
            Thread.sleep(1000);
            Log.d("Teamcode", "pub 1 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
            robot.transfer.next();
            robot.transfer.intake.turnWristManualLeft();
            Log.d("Teamcode", "pub 2 - Expected: HOVERG; Actual: " + robot.transfer.stateMachine.getCurrentState());
            Thread.sleep(500);
            robot.transfer.next();
            Log.d("Teamcode", "pub 3 - Expected: GRABG; Actual: " + robot.transfer.stateMachine.getCurrentState());
            Thread.sleep(500);
            synchronized (lock) {
                Log.i("Teamcode", "pickUpBlock finished, notifying main thread");
                lock.notify();
            }
        } catch (InterruptedException err) {
            Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
        }
    }), dropBlock = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
                robot.transfer.switchToSpecimen();
                Log.d("Teamcode", "dp 1 - Expected: HOVERW; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(250);
                synchronized (lock) {
                    Log.i("Teamcode", "dropBlock finished, notifying main thread");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }
    ), placeOnBar = CompletableFuture.runAsync(() -> {
            try {
                Log.d("Teamcode", "pob 1 - Expected: GRABW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_CHAMBER);
                robot.vSlides.loop();
                Log.d("Teamcode", "pob 2 - Expected: SPECIMENSCORE, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(1500);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
                robot.vSlides.loop();
                robot.transfer.next();
                Log.d("Teamcode", "pob 3 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                synchronized (lock) {
                    Log.i("Teamcode", "placeOnBar finished, notifying main thread");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        }), pickOffWall = CompletableFuture.runAsync(() -> {
            try {
                Log.d("Teamcode", "pow 1 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.transfer.next();
                Log.d("Teamcode", "pow 2 - Expected: GRABW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(250);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
                robot.vSlides.loop();
                // Enough to lift it off the wall
                Thread.sleep(500);
                synchronized (lock) {
                    Log.i("Teamcode", "pickOffWall finished, notifying main thread");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
            }
        });

    public void buildPaths() {
        // NOTES:
        // * pickUpBlock takes you to GRABG
        // * pickOffWall takes you from HOVERW to GRABW
        // * placeOnBar takes you from GRABW to HOVERW

        start2bot = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(startPose), new Point(botSample)))
                .addParametricCallback(0, () -> {
                    robot.hSlides.moveToPosition(HSlides.SlidePositions.IN);
                    robot.hSlides.loop();
                })
                .addParametricCallback(0, pickUpBlock::join)
                .setLinearHeadingInterpolation(startPose.getHeading(), botSample.getHeading())
                .build();

        bot2drop = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botSample), new Point(botDrop)))
                .addParametricCallback(0, () -> {
                    robot.hSlides.hold();
                    robot.hSlides.loop();
                })
                .addParametricCallback(1, dropBlock::join)
                .setLinearHeadingInterpolation(botSample.getHeading(), botDrop.getHeading())
                .build();

        drop2mid = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botDrop), new Point(midSample)))
                .addParametricCallback(1, pickUpBlock::join)
                .setLinearHeadingInterpolation(botDrop.getHeading(), midSample.getHeading())
                .build();

        mid2drop = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(midSample), new Point(midDrop)))
                .addParametricCallback(1, dropBlock::join)
                .setLinearHeadingInterpolation(midSample.getHeading(), midDrop.getHeading())
                .build();

        drop2top = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(midDrop), topControl, new Point(topSample)))
                .addParametricCallback(1, pickUpBlock::join)
                .setLinearHeadingInterpolation(midDrop.getHeading(), topSample.getHeading())
                .build();

        top2drop = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(topSample), topControl, new Point(topDrop)))
                .addParametricCallback(1, dropBlock::join)
                .setLinearHeadingInterpolation(topSample.getHeading(), topDrop.getHeading())
                .build();

    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(start2bot);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    try {
                        Log.i("Teamcode", "Main thread locking...");
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.get();
                        Log.i("Teamcode", "Main thread notified");
                    } catch (InterruptedException | ExecutionException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(bot2drop, true);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    try {
                        Log.i("Teamcode", "Main thread locking...");
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.get();
                        Log.i("Teamcode", "Main thread notified");
                    } catch (InterruptedException | ExecutionException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(drop2mid, true);
                    setPathState(3);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    try {
                        Log.i("Teamcode", "Main thread locking...");
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.get();
                        Log.i("Teamcode", "Main thread notified");
                    } catch (InterruptedException | ExecutionException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(mid2drop, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    try {
                        Log.i("Teamcode", "Main thread locking...");
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.get();
                        Log.i("Teamcode", "Main thread notified");
                    } catch (InterruptedException | ExecutionException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(drop2top, true);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    try {
                        Log.i("Teamcode", "Main thread locking...");
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.get();
                        Log.i("Teamcode", "Main thread notified");
                    } catch (InterruptedException | ExecutionException err) {
                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
                    }
                    follower.followPath(top2drop, true);
                    setPathState(7);
                }
                break;
            case 7:
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
