package com.pathplanner.lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.lib.util.AngleUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//TODO this hackery because PathPlanner doesnt deal with the stupid mirroring of the gamefield
public class TransformUtil {

    public static PathPlannerTrajectory.PathPlannerState transformState(PathPlannerTrajectory.PathPlannerState state, DriverStation.Alliance alliance) {
        try {
            return transformStateForAlliance(state, alliance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     static PathPlannerTrajectory.PathPlannerState transformStateForAlliance(PathPlannerTrajectory.PathPlannerState state, DriverStation.Alliance alliance) throws NoSuchFieldException, IllegalAccessException {
         // Create a new state so that we don't overwrite the original
         boolean red = alliance == DriverStation.Alliance.Red;
         double coef = 1;
         if (!red) {
             coef = -1;
         }


         PathPlannerTrajectory.PathPlannerState transformedState = new PathPlannerTrajectory.PathPlannerState();


         Translation2d transformedTranslation;
         if (red) {
             transformedTranslation =
                     new Translation2d(16.48 - state.poseMeters.getX(), state.poseMeters.getY());
         } else {
             transformedTranslation =
                     state.poseMeters.getTranslation();
         }


         System.out.println(String.format("Pre: Heading %s | Holo %s", state.poseMeters.getRotation().getDegrees(), state.holonomicRotation.getDegrees()));

         Rotation2d transformedHeading;
         //zero clue why this works
         if (red) {
             transformedHeading = state.poseMeters.getRotation();
         } else {
             transformedHeading = state.poseMeters.getRotation().rotateBy(Rotation2d.fromDegrees(180));
         }
         Rotation2d transformedHolonomicRotation;
         if (red) {
             transformedHolonomicRotation = state.holonomicRotation;//.rotateBy(Rotation2d.fromDegrees(180));
         } else {
             transformedHolonomicRotation = state.holonomicRotation.rotateBy(Rotation2d.fromDegrees(180));//.rotateBy(Rotation2d.fromDegrees(180));
         }
          //This needs to rotate for some reason LOL


         transformedState.timeSeconds = state.timeSeconds;
         transformedState.velocityMetersPerSecond = state.velocityMetersPerSecond;
         transformedState.accelerationMetersPerSecondSq = state.accelerationMetersPerSecondSq;
         transformedState.poseMeters = new Pose2d(transformedTranslation, transformedHeading);



         transformedState.angularVelocityRadPerSec = coef * state.angularVelocityRadPerSec;
         transformedState.holonomicRotation = transformedHolonomicRotation;
         transformedState.holonomicAngularVelocityRadPerSec = coef * state.holonomicAngularVelocityRadPerSec;
         Field curveRadius = PathPlannerTrajectory.PathPlannerState.class.getDeclaredField("curveRadius");
         curveRadius.setAccessible(true);//Very important, this allows the setting to work.
         curveRadius.set(transformedState, coef * (double)curveRadius.get(transformedState));
         transformedState.curvatureRadPerMeter = coef * state.curvatureRadPerMeter;
         Field deltaPos = PathPlannerTrajectory.PathPlannerState.class.getDeclaredField("deltaPos");
         deltaPos.setAccessible(true);//Very important, this allows the setting to work.
         deltaPos.set(transformedState, deltaPos.get(transformedState));



         return transformedState;
    }


    public static PathPlannerTrajectory transform(PathPlannerTrajectory trajectory, DriverStation.Alliance alliance) {
        try {
            return transformTrajectoryForAlliance(trajectory, alliance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static PathPlannerTrajectory transformTrajectoryForAlliance(
            PathPlannerTrajectory trajectory, DriverStation.Alliance alliance) throws NoSuchFieldException, IllegalAccessException {
        List<Trajectory.State> transformedStates = new ArrayList<>();

        for (Trajectory.State s : trajectory.getStates()) {
            PathPlannerTrajectory.PathPlannerState state = (PathPlannerTrajectory.PathPlannerState) s;

            transformedStates.add(transformStateForAlliance(state, alliance));
        }

        return new PathPlannerTrajectory(
                transformedStates,
                trajectory.getMarkers(),
                trajectory.getStartStopEvent(),
                trajectory.getEndStopEvent(),
                trajectory.fromGUI);
    }

}
