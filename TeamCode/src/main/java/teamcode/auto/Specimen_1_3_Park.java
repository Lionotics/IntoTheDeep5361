package teamcode.auto;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.Consts;
import teamcode.hardware.HSlides;
import teamcode.hardware.Intake;
import teamcode.hardware.Robot;
import teamcode.hardware.StateMachine;
import teamcode.hardware.VSlides;

@Autonomous(name = "Specimen 1+3 Park", group = "Red")
public class Specimen_1_3_Park extends OpMode {
    public static Pose startPose = new Pose(135.75, 88, Math.toRadians(0));
    public static Pose bar = new Pose(117.75,79, Math.toRadians(0));
    public static Pose botSample = new Pose(115.75, 106, Math.toRadians(140));
    public static Pose botDrop = new Pose(115.75, 106, Math.toRadians(55));
    public static Pose midSample = new Pose(115.75, 117, Math.toRadians(135));
    public static Pose midDrop = new Pose(115.75, 117, Math.toRadians(55));
    public static Pose topSample = new Pose(115.75, 126, Math.toRadians(130));
    public static Pose topDrop = new Pose(115.75, 126, Math.toRadians(55));
    public static Point topControl = new Point(115.75, 117, Point.CARTESIAN);
    //    private int iteration = 1; private final int MAX_ITERATION = 4;
    public static int waitTime = 1000;
    private static final Object lock = new Object();
    private static int pathState = 0;
    public Robot robot = Robot.getInstance();
    private Follower follower;
    private PathChain start2bar/*,start2bot, bot2drop, drop2mid, mid2drop, drop2top, top2drop*/;
    private Intake intake;

    private final Thread pickUpBlock = new Thread() {
        @Override
        public void run() {
            try {
                robot.transfer.switchToSample(true);
                Log.d("Teamcode", "pub 1 - Expected: BARRIER; Actual: " + robot.transfer.stateMachine.getCurrentState());
                robot.transfer.next(true);
                intake.turnWristManualLeft();
                Log.d("Teamcode", "pub 2 - Expected: HOVERG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                robot.transfer.next(true);
                Log.d("Teamcode", "pub 3 - Expected: GRABG; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                synchronized (lock) {
                    Log.d("Teamcode", "pickUpBlock finished and unlocking");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        }
    };

    private final Thread dropBlock = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
                robot.transfer.switchToSpecimen(true);
                Log.d("Teamcode", "dp 1 - Expected: HOVERW; Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(250);
                Log.d("Teamcode", "dp finished");
                synchronized (lock) {
                    Log.d("Teamcode", "dropBlock finished and unlocking");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        }
    };

    private final Thread placeOnBar = new Thread() {
        @Override
        public void run() {
            try {
                Log.d("Teamcode", "pob 1 - Expected: GRABW or START, Actual: " + robot.transfer.stateMachine.getCurrentState());
                if (pathState > 2) {
                robot.transfer.next(true);
                } else {
                    robot.transfer.switchToSpecimenscore();
                    robot.transfer.ee.setClaw(Consts.E_CLAW_CLOSE);
                }
                Thread.sleep(5000);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_CHAMBER);
                robot.vSlides.loop();
                Thread.sleep(5000);
                Log.d("Teamcode", "pob 2 - Expected: SPECIMENSCORE, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(5000);
                robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM_BUCKET);
                robot.vSlides.loop();
                Thread.sleep(5000);
                robot.transfer.next(true);
                Thread.sleep(5000);
                Log.d("Teamcode", "pob 3 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Log.d("Teamcode", "pob finished");
                synchronized (lock) {
                    Log.d("Teamcode", "placeOnBar finished and unlocking");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        }
    };

    private final Thread pickOffWall = new Thread() {
        @Override
        public void run() {
            try {
                Log.d("Teamcode", "pow 1 - Expected: HOVERW, Actual: " + robot.transfer.stateMachine.getCurrentState());
                Thread.sleep(500);
                Log.d("Teamcode", "pow finished");
                synchronized (lock) {
                    Log.d("Teamcode", "pickOffWall finished and unlocking");
                    lock.notify();
                }
            } catch (InterruptedException err) {
                Log.e("Teamcode", "Thread failed; Interrupting Thread...");
                Thread.currentThread().interrupt();
            }
        }
    };

    public void buildPaths () {
        // NOTES:
        // * pickUpBlock takes you to GRABG
        // * pickOffWall takes you from HOVERW to GRABW
        // * placeOnBar takes you from GRABW to HOVERW

        start2bar = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(startPose), new Point(bar)))
                .setLinearHeadingInterpolation(startPose.getHeading(), bar.getHeading())
                .addParametricCallback(.99, placeOnBar::start)
                .build();

//        start2bot = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(startPose), new Point(botSample)))
//                .addParametricCallback(.99, () -> {
//                    robot.hSlides.moveToPosition(HSlides.SlidePositions.IN);
//                    robot.hSlides.loop();
//                    pickUpBlock.start();
//                })
//                .setLinearHeadingInterpolation(startPose.getHeading(), botSample.getHeading())
//                .build();
//
//        bot2drop = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(botSample), new Point(botDrop)))
//                .addParametricCallback(0, () -> {
//                    robot.hSlides.hold();
//                    robot.hSlides.loop();
//                })
//                .addParametricCallback(.99, dropBlock::start)
//                .setPathEndTValueConstraint(.99)
//                .setLinearHeadingInterpolation(botSample.getHeading(), botDrop.getHeading())
//                .build();
//
//        drop2mid = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(botDrop), new Point(midSample)))
//                .addParametricCallback(.99, pickUpBlock::start)
//                .setLinearHeadingInterpolation(botDrop.getHeading(), midSample.getHeading())
//                .build();
//
//        mid2drop = follower.pathBuilder().addPath(new BezierCurve(new Point(midSample), new Point(midDrop)))
//                .addParametricCallback(.99, dropBlock::start)
//                .setLinearHeadingInterpolation(midSample.getHeading(), midDrop.getHeading())
//                .build();
//
//        drop2top = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(midDrop), topControl, new Point(topSample)))
//                .addParametricCallback(.99, pickUpBlock::start)
//                .setLinearHeadingInterpolation(midDrop.getHeading(), topSample.getHeading())
//                .build();
//
//        top2drop = follower.pathBuilder()
//                .addPath(new BezierCurve(new Point(topSample), topControl, new Point(topDrop)))
//                .addParametricCallback(.99, dropBlock::start)
//                .setLinearHeadingInterpolation(topSample.getHeading(), topDrop.getHeading())
//                .build();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(start2bar);
                setPathState(1);
                break;
//            case 0:
//                follower.followPath(start2bot);
//                setPathState(1);
//                break;
//            case 1:
//                if (!follower.isBusy()) {
//                    try {
//                        Log.i("Teamcode", "Main thread locked for start2bot");
//                        synchronized (lock) {
//                            lock.wait();
//                            Log.i("Teamcode", "Main thread released for start2bot");
//                        }
//                    } catch (InterruptedException err) {
//                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
//                    }
//                    follower.followPath(bot2drop, true);
//                    setPathState(2);
//                }
//                break;
//            case 2:
//                if (!follower.isBusy()) {
//                    try {
//                        Log.i("Teamcode", "Main thread locked for bot2drop");
//                        synchronized (lock) {
//                            lock.wait();
//                            Log.i("Teamcode", "Main thread released for bot2drop");
//                        }
//                    } catch (InterruptedException err) {
//                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
//                    }
//                    follower.followPath(drop2mid, true);
//                    setPathState(3);
//                } else {
//                    Log.i("Teamcode", "follower is busy- waiting on bot2drop");
//                }
//                break;
//            case 3:
//                if (!follower.isBusy()) {
//                    try {
//                        Log.i("Teamcode", "Main thread locked for drop2mid");
//                        synchronized (lock) {
//                            lock.wait();
//                            Log.i("Teamcode", "Main thread released for drop2mid");
//                        }
//                    } catch (InterruptedException err) {
//                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
//                    }
//                    follower.followPath(mid2drop, true);
//                    setPathState(4);
//                }
//                break;
//            case 4:
//                if (!follower.isBusy()) {
//                    try {
//                        Log.i("Teamcode", "Main thread locked for mid2drop");
//                        synchronized (lock) {
//                            lock.wait();
//                            Log.i("Teamcode", "Main thread released for mid2drop");
//                        }
//                    } catch (InterruptedException err) {
//                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
//                    }
//                    follower.followPath(drop2top, true);
//                    setPathState(5);
//                }
//                break;
//            case 5:
//                if (!follower.isBusy()) {
//                    try {
//                        Log.i("Teamcode", "Main thread locked for drop2top");
//                        synchronized (lock) {
//                            lock.wait();
//                            Log.i("Teamcode", "Main thread released for drop2top");
//                        }
//                    } catch (InterruptedException err) {
//                        Log.e("Failed to handle multi threading{}", Arrays.toString(err.getStackTrace()));
//                    }
//                    follower.followPath(top2drop, true);
//                    setPathState(6);
//                }
//                break;
//            case 6:
            case 1:
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
    @Override public void loop() {
        // These loop the movements of the robot
        follower.update();

        autonomousPathUpdate();
        StateMachine.State state = robot.transfer.stateMachine.getCurrentState();
        robot.hSlides.hold();

        // Feedback to Driver Hub
        follower.telemetryDebug(telemetry);
        telemetry.addData("path state", pathState);
        telemetry.addData("State", state.name());
        /*telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());*/
        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override public void init() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap);
        robot.transfer.flush();
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
    @Override public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override public void start() {
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override public void stop() {
        synchronized (lock) {
            lock.notify();
        }
    }
}