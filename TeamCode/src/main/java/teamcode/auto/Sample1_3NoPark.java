package teamcode.auto;

import static java.lang.Thread.sleep;

import android.util.Log;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.EndEffector;
import teamcode.hardware.Intake;
import teamcode.hardware.Robot;
import teamcode.hardware.VSlides;

@Autonomous(name = "Sample 1 + 3 No Park", group = "Red")
public class Sample1_3NoPark extends OpMode {
    public static Pose startPose = new Pose(133, 36, Math.toRadians(90));
    public static Pose basket = new Pose(128.2946175637394, 7.546742209631725, Math.toRadians(135));
    public static Pose topSample = new Pose(113, 20, Math.toRadians(180));
    public static Pose midSample = new Pose(113, 15, Math.toRadians(180));
    public static Pose botSample = new Pose(113, 15, Math.toRadians(225));

    private static int pathState = 0;
    private Follower follower;
    private PathChain preload2basket, basket2top, top2basket, basket2mid, mid2basket, basket2bot,
        bot2basket;

    public Robot robot = Robot.getInstance();
    public void buildPaths() {

        preload2basket = follower.pathBuilder()
                .addPath(new Path(new BezierLine(new Point(startPose), new Point(basket))))
                .addParametricCallback(0, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
                    robot.transfer.ee.setBigPivot(EndEffector.EEConsts.BIG_SAMPLE);
                    robot.transfer.ee.setLittlePivot(EndEffector.EEConsts.LITTLE_SAMPLE);
                    robot.transfer.intake.setWrist(Intake.IntakeConsts.WRIST_UP);
                    robot.transfer.intake.setPivot(Intake.IntakeConsts.PIVOT_TRANSFER);
                })
                .setLinearHeadingInterpolation(startPose.getHeading(), basket.getHeading())
                .addParametricCallback(1, () -> {
                    robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                })
                .build();

        basket2top = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(topSample)))
                .addParametricCallback(0, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                })
                .setLinearHeadingInterpolation(basket.getHeading(), topSample.getHeading())
                .addTemporalCallback(500, () -> {
                    robot.transfer.next(); robot.transfer.next(); robot.transfer.next();
                    robot.transfer.next(); // Pick up block and transfer
                })
                .build();

        top2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(topSample), new Point(basket)))
                .addParametricCallback(0, () -> {
                    robot.transfer.next();
                })
                .setLinearHeadingInterpolation(topSample.getHeading(), basket.getHeading())
                .addTemporalCallback(1500, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
                })
                .addTemporalCallback(2000, () -> {
                    robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                })
                .build();

        basket2mid = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(midSample)))
                .addParametricCallback(0, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                })
                .setLinearHeadingInterpolation(basket.getHeading(), midSample.getHeading())
                .addTemporalCallback(500, () -> {
                    robot.transfer.next(); robot.transfer.next(); robot.transfer.next();
                    robot.transfer.next(); // Pick up block and transfer
                })
                .build();

        mid2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midSample), new Point(basket)))
                .addParametricCallback(0, () -> {
                    robot.transfer.next();
                })
                .setLinearHeadingInterpolation(midSample.getHeading(), basket.getHeading())
                .addTemporalCallback(1500, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
                })
                .addTemporalCallback(2000, () -> {
                    robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);
                })
                .build();

        basket2bot = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(botSample)))
                .addParametricCallback(0, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.BOTTOM);
                })
                .setLinearHeadingInterpolation(basket.getHeading(), botSample.getHeading())
                .addTemporalCallback(500, () -> {
                    robot.transfer.next(); robot.transfer.next();
                    robot.transfer.intake.turnWristManualRight();
                })
                .addTemporalCallback(1000, () -> {
                    robot.transfer.next(); robot.transfer.next();
                })
                .build();

        bot2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(botSample), new Point(basket)))
                .addParametricCallback(0, () -> {
                    robot.transfer.next();
                })
                .setLinearHeadingInterpolation(botSample.getHeading(), basket.getHeading())
                .addTemporalCallback(1500, () -> {
                    robot.vSlides.moveToPosition(VSlides.LiftPositions.TOP_BUCKET);
                })
                .addTemporalCallback(2000, () -> {
                    robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_OPEN);})
                .build();
    }

   public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(preload2basket);
                setPathState(1);
                break;
            case 1:
                if(!follower.isBusy()) {
                    follower.followPath(basket2top,true);
                    setPathState(2);
                }
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(top2basket,true);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    follower.followPath(basket2mid,true);
                    setPathState(4);
                }
                break;
            case 4:
                if(!follower.isBusy()) {
                    follower.followPath(mid2basket,true);
                    setPathState(5);
                }
                break;
            case 5:
                if(!follower.isBusy()) {
                    follower.followPath(basket2bot,true);
                    setPathState(6);
                }
                break;
            case 6:
                if(!follower.isBusy()) {
                    follower.followPath(bot2basket,true);
                    setPathState(7);
                }
                break;
            case 7:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if(!follower.isBusy()) {
                    /* Level 1 Ascent */

                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                    setPathState(-1);
                }
                break;
        }
    }


    public void setPathState(int pState) {
        pathState = pState;
    }

    /** This is the main loop of the OpMode, it will run repeatedly after clicking "Play". **/
    @Override
    public void loop() {

        // These loop the movements of the robot
        follower.update();
        //robot.vSlides.loop();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /** This method is called once at the init of the OpMode. **/
    @Override
    public void init() {
        robot.init(hardwareMap);
        Log.d("Teamcode", "Entered init callback");
        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();

        robot.transfer.ee.setClaw(EndEffector.EEConsts.CLAW_CLOSE);
    }

    /** This method is called continuously after Init while waiting for "play". **/
    @Override
    public void init_loop() {}

    /** This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system **/
    @Override
    public void start() {
        setPathState(0);
    }

    /** We do not use this because everything should automatically disable **/
    @Override
    public void stop() {
    }

}


