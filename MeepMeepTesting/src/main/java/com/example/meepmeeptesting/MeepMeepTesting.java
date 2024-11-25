package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 16.4)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(47, 62, Math.toRadians(180)))
                .stopAndAdd(new SleepAction(4)) // Raise the slides, open the claw
                .splineToSplineHeading(new Pose2d(58.4, 35.5, Math.toRadians(-90)), Math.toRadians(0))
                .stopAndAdd(new SleepAction(1)) // Pick up middle sample
                .splineToSplineHeading(new Pose2d(58.5, 62, Math.toRadians(235)), 0)
                .stopAndAdd(new SleepAction(1)) // Drop middle sample
                .splineToSplineHeading(new Pose2d(48, 35.5, Math.toRadians(-90)), Math.toRadians(0))
                .stopAndAdd(new SleepAction(1)) // Pick up left sample
                .splineToSplineHeading(new Pose2d(58.5, 62, Math.toRadians(235)), 0)
                .stopAndAdd(new SleepAction(1)) // Drop left sample
                .splineToSplineHeading(new Pose2d(58.5, 26, Math.toRadians(0)), Math.toRadians(0))
                .stopAndAdd(new SleepAction(1)) // Pick up right sample
                .strafeTo(new Vector2d(52, 45)) // Get away from the wall
                .splineToSplineHeading(new Pose2d(58.5, 62, Math.toRadians(235)), 0)
                .stopAndAdd(new SleepAction(1)) // Drop right sample
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_LIGHT)
                .setDarkMode(false)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}