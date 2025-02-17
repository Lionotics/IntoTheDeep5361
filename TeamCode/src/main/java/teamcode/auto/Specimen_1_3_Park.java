package teamcode.auto;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.Consts;
import teamcode.hardware.HSlides;
import teamcode.hardware.Intake;
import teamcode.hardware.StateMachine;
import teamcode.hardware.VSlides;

@Autonomous(name = "Specimen 1+3 Park", group = "Red")
public class Specimen_1_3_Park extends OpMode {
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static Pose startPose = new Pose(135.75, 88, Math.toRadians(180));
    public static Pose botSample = new Pose(115.75, 106, Math.toRadians(140));
    public static Pose botDrop = new Pose(115.75, 106, Math.toRadians(55));
    public static Pose midSample = new Pose(115.75, 117, Math.toRadians(135));
    public static Pose midDrop = new Pose(115.75, 117, Math.toRadians(55));
    public static Pose topSample = new Pose(115.75, 126, Math.toRadians(130));
    public static Pose topDrop = new Pose(115.75, 126, Math.toRadians(55));
    public static Point topControl = new Point(115.75, 117, Point.CARTESIAN);
    private static int pathState = 0;
    public Robot robot = Robot.getInstance();
    //    private int iteration = 1; private final int MAX_ITERATION = 4;
    public static int waitTime = 1000;
    private Follower follower;
    private PathChain start2bot, bot2drop, drop2mid, mid2drop, drop2top, top2drop;
    private Intake intake;

    private Runnable pickUpBlock() {
        return () -> {
            try {
                robot.transfer.switchToSample();
                Log.d("Teamcode", "pub 1 - Expected: BARRIER; Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                intake.turnWristManualLeft();
                Log.d("Teamcode", "pub 2 - Expected: HOVERG; Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.transfer.next();
                Log.d("Teamcode", "pub 3 - Expected: GRABG; Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        };
    };
    private Runnable dropBlock() {
        return () -> {
            try {
                Thread.sleep(500);
                robot.transfer.switchToSpecimen();
                Log.d("Teamcode", "dp 1 - Expected: HOVERW; Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(250);
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        };
    };
    private Runnable placeOnBar() {
        return () -> {
            try {
                Log.d("Teamcode", "pob 1 - Expected: GRABW, Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next();
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_CHAMBER);
                robot.vSlides.loop();
                Log.d("Teamcode", "pob 2 - Expected: SPECIMENSCORE, Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(1500);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
                robot.vSlides.loop();
                robot.transfer.next();
                Log.d("Teamcode", "pob 3 - Expected: HOVERW, Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        };
    };
    private Runnable pickOffWall() {
        return () -> {
            try {
                Log.d("Teamcode", "pow 1 - Expected: HOVERW, Actual: " +
                        robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.vSlides.loop();
                // Enough to lift it off the wall
                Thread.sleep(500);
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        };
    };

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
                .addParametricCallback(1, () -> executor.submit(this::pickUpBlock))
                .setLinearHeadingInterpolation(startPose.getHeading(), botSample.getHeading())
                .build();

        bot2drop = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botSample), new Point(botDrop)))
                .addParametricCallback(0, () -> {
                    robot.hSlides.hold();
                    robot.hSlides.loop();
                })
                .addParametricCallback(1, () -> executor.submit(this::dropBlock))
                .setLinearHeadingInterpolation(botSample.getHeading(), botDrop.getHeading())
                .build();

        drop2mid = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botDrop), new Point(midSample)))
                .addParametricCallback(1, () -> executor.submit(this::pickUpBlock))
                .setLinearHeadingInterpolation(botDrop.getHeading(), midSample.getHeading())
                .build();

        mid2drop = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(midSample), new Point(midDrop)))
                .addParametricCallback(1, () -> executor.submit(this::dropBlock))
                .setLinearHeadingInterpolation(midSample.getHeading(), midDrop.getHeading())
                .build();

        drop2top = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(midDrop), topControl, new Point(topSample)))
                .addParametricCallback(1, () -> executor.submit(this::pickUpBlock))
                .setLinearHeadingInterpolation(midDrop.getHeading(), topSample.getHeading())
                .build();

        top2drop = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(topSample), topControl, new Point(topDrop)))
                .addParametricCallback(1, () -> executor.submit(this::dropBlock))
                .setLinearHeadingInterpolation(topSample.getHeading(), topDrop.getHeading())
                .build();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(start2bot);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException err) {
                        Log.e("Teamcode","Timeout reached, forcing shutdown.");
                        executor.shutdownNow(); // Attempts to stop execution
                    }
                    executor = Executors.newSingleThreadExecutor();
                    follower.followPath(bot2drop, true);
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException err) {
                        Log.e("Teamcode","Timeout reached, forcing shutdown.");
                        executor.shutdownNow(); // Attempts to stop execution
                    }
                    executor = Executors.newSingleThreadExecutor();
                    follower.followPath(drop2mid, true);
                    setPathState(3);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException err) {
                        Log.e("Teamcode","Timeout reached, forcing shutdown.");
                        executor.shutdownNow(); // Attempts to stop execution
                    }
                    executor = Executors.newSingleThreadExecutor();
                    follower.followPath(mid2drop, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException err) {
                        Log.e("Teamcode","Timeout reached, forcing shutdown.");
                        executor.shutdownNow(); // Attempts to stop execution
                    }
                    executor = Executors.newSingleThreadExecutor();
                    follower.followPath(drop2top, true);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException err) {
                        Log.e("Teamcode","Timeout reached, forcing shutdown.");
                        executor.shutdownNow(); // Attempts to stop execution
                    }
                    executor = Executors.newSingleThreadExecutor();
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

    private void setPathState(int pState) {
        pathState = pState;
    }

    /**
     * This method is called once at the init of the OpMode.
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

        intake = robot.transfer.intake;

        robot.transfer.ee.setClaw(Consts.E_CLAW_CLOSE);
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
        executor.shutdownNow();
    }
}