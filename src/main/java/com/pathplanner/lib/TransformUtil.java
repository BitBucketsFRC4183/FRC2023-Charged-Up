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

     static PathPlannerTrajectory.PathPlannerState transformStateForAlliance(
            PathPlannerTrajectory.PathPlannerState state, DriverStation.Alliance alliance) throws NoSuchFieldException, IllegalAccessException {
        if (alliance == DriverStation.Alliance.Red) {
            // Create a new state so that we don't overwrite the original
            PathPlannerTrajectory.PathPlannerState transformedState = new PathPlannerTrajectory.PathPlannerState();

            Translation2d transformedTranslation =
                    new Translation2d(16.48 - state.poseMeters.getX(), state.poseMeters.getY());

            System.out.println(String.format("Pre: Heading %s | Holo %s", state.poseMeters.getRotation().getDegrees(), state.holonomicRotation.getDegrees()));

            Rotation2d transformedHeading = state.poseMeters.getRotation();
            Rotation2d transformedHolonomicRotation = state.holonomicRotation.rotateBy(Rotation2d.fromDegrees(180));

            System.out.println(String.format("Post: Heading %s | Holo %s", transformedHeading.getDegrees(), transformedHolonomicRotation.getDegrees()));

            transformedState.timeSeconds = state.timeSeconds;
            transformedState.velocityMetersPerSecond = state.velocityMetersPerSecond;
            transformedState.accelerationMetersPerSecondSq = state.accelerationMetersPerSecondSq;
            transformedState.poseMeters = new Pose2d(transformedTranslation, transformedHeading);
            transformedState.angularVelocityRadPerSec = -state.angularVelocityRadPerSec;
            transformedState.holonomicRotation = transformedHolonomicRotation;
            transformedState.holonomicAngularVelocityRadPerSec = -state.holonomicAngularVelocityRadPerSec;
            Field curveRadius = PathPlannerTrajectory.PathPlannerState.class.getDeclaredField("curveRadius");
            curveRadius.setAccessible(true);//Very important, this allows the setting to work.
            curveRadius.set(transformedState, -(double)curveRadius.get(transformedState));
            transformedState.curvatureRadPerMeter = -state.curvatureRadPerMeter;
            Field deltaPos = PathPlannerTrajectory.PathPlannerState.class.getDeclaredField("deltaPos");
            deltaPos.setAccessible(true);//Very important, this allows the setting to work.
            deltaPos.set(transformedState, deltaPos.get(transformedState));

            return transformedState;
        } else {
            return state;
        }
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
        if (alliance == DriverStation.Alliance.Red) {
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
        } else {
            return trajectory;
        }
    }

}
