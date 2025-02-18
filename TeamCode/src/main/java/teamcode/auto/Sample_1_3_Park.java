package teamcode.auto;

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
import com.rowanmcalpin.nextftc.core.command.groups.ParallelGroup;
import com.rowanmcalpin.nextftc.core.command.groups.SequentialGroup;
import com.rowanmcalpin.nextftc.core.command.utility.NullCommand;
import com.rowanmcalpin.nextftc.core.command.utility.delays.Delay;
import com.rowanmcalpin.nextftc.pedro.FollowPath;
import com.rowanmcalpin.nextftc.pedro.PedroOpMode;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
import teamcode.hardware.Consts;
import teamcode.hardware.EndEffector;
import teamcode.hardware.HSlides;
import teamcode.hardware.Intake;
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
    private PathChain preload2basket, basket2top, top2basket, basket2mid, mid2basket, basket2bot,
            bot2basket, basket2park, park2start;
    private boolean shouldRestart = false;

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
                .build();

        basket2top = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(topSample)))
                .setLinearHeadingInterpolation(basket.getHeading(), topSample.getHeading())
                .build();

        top2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(topSample), new Point(basket)))
                .setLinearHeadingInterpolation(topSample.getHeading(), basket.getHeading())
                .build();

        basket2mid = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(midSample)))
                .setLinearHeadingInterpolation(basket.getHeading(), midSample.getHeading())
                .build();

        mid2basket = follower.pathBuilder()
                .addPath(new BezierLine(new Point(midSample), new Point(basket)))
                .setLinearHeadingInterpolation(midSample.getHeading(), basket.getHeading())
                .build();

        basket2bot = follower.pathBuilder()
                .addPath(new BezierLine(new Point(basket), new Point(botSample)))
                .setLinearHeadingInterpolation(basket.getHeading(), botSample.getHeading())
                .build();
        bot2basket = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(botSample), new Point(botBackup)))
                .setLinearHeadingInterpolation(botSample.getHeading(), botBackup.getHeading())
                .addPath(new BezierLine(new Point(botBackup), new Point(basket)))
                .setLinearHeadingInterpolation(botBackup.getHeading(), basket.getHeading())
                .build();

        basket2park = follower.pathBuilder()
                .addPath(new BezierCurve(new Point(basket), new Point(topSample)))
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
        Command start = new NullCommand();
        if (shouldRestart) {
            start = new FollowPath(park2start, true);
        }
        return new SequentialGroup(
                new ParallelGroup(
                        new FollowPath(preload2basket),
                        new SequentialGroup(
                                new ParallelGroup(
                                        EndEffector.INSTANCE.setBigPivot(Consts.BIG_SAMPLE),
                                        EndEffector.INSTANCE.setLittlePivot(Consts.LITTLE_SAMPLE),
                                        Intake.INSTANCE.turnWristTo(Intake.WristState.NORTH),
                                        Intake.INSTANCE.setPivot(Consts.PIVOT_TRANSFER)
                                ),
                                new Delay(2), //TODO: This is way too high because this is two after big finishes
                                EndEffector.INSTANCE.setClaw(Consts.E_CLAW_OPEN),
                                new Delay(1.25)
                        )
                ),
                new ParallelGroup(
                        new FollowPath(basket2top, true),
                        pickUpBlock(false)
                ),
                new ParallelGroup(
                        new FollowPath(top2basket, true),
                        placeInBucket()
                ),
                new ParallelGroup(
                        new FollowPath(basket2mid, true),
                        pickUpBlock(false)
                ),
                new ParallelGroup(
                        new FollowPath(mid2basket, true),
                        placeInBucket()
                ),
                new ParallelGroup(
                        new FollowPath(basket2bot, true),
                        pickUpBlock(true)
                ),
                new ParallelGroup(
                        new FollowPath(bot2basket, true),
                        placeInBucket()
                ),
                new ParallelGroup(
                        new FollowPath(basket2park, true),
                        Transfer.INSTANCE.next(),
                        VSlides.INSTANCE.moveToBottom()
                ),
                start
        );
    }

    // Transfer to Barrier
    private Command placeInBucket() {
        return new SequentialGroup(
                Transfer.INSTANCE.next(), // Move from TRANSFER to SAMPLESCORE
                VSlides.INSTANCE.moveToTopBucket(),
                EndEffector.INSTANCE.setClaw(Consts.E_CLAW_OPEN),
                new Delay(1),
                Transfer.INSTANCE.next() // Move from SAMPLESCORE to BARRIER
        );
    }

    // Barrier to Transfer
    private Command pickUpBlock(boolean turnedRight) {
        Command turn = new NullCommand();
        if (turnedRight) {
            turn = new SequentialGroup(
                    Intake.INSTANCE.turnWristManualRight(),
                    new Delay(0.5),
                    Intake.INSTANCE.setClaw(Consts.I_CLAW_CLOSE),
                    new Delay(.5)
            );
        }
        return new SequentialGroup(
                VSlides.INSTANCE.moveToBottom(),
                Transfer.INSTANCE.next(), // Move from BARRIER to HOVERG
                turn,
                new Delay(.5),
                Transfer.INSTANCE.next(), // Move from HOVERG to GRABG
                new Delay(.75),
                Transfer.INSTANCE.next(), // Move from GRABG to TRANSFER
                new Delay(.75)
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
        Transfer.INSTANCE.startAuto().invoke();
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     * Use this to collect info from the gamepad to change variables.
     **/
    @Override
    public void onWaitForStart() {
        if (gamepad1.a) {
            shouldRestart = true;
        } else if (gamepad1.b) {
            shouldRestart = false;
        }
        telemetry.addData("Restart a/b", shouldRestart);
        telemetry.update();
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


