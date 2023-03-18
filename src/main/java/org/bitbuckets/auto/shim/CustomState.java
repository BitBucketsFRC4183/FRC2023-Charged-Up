package org.bitbuckets.auto.shim;

import com.pathplanner.lib.GeometryUtil;
import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;

public class CustomState extends Trajectory.State {


    static double doubleLerp(double startVal, double endVal, double t) {
        return startVal + (endVal - startVal) * t;
    }
    static Rotation2d rotationLerp(Rotation2d startVal, Rotation2d endVal, double t) {
        return startVal.plus(endVal.minus(startVal).times(t));
    }

    static Translation2d translationLerp(Translation2d a, Translation2d b, double t) {
        return a.plus((b.minus(a)).times(t));
    }


    public double angularVelocityRadPerSec = 0;
    public Rotation2d holonomicRotation = new Rotation2d();
    public double holonomicAngularVelocityRadPerSec = 0;

    private double curveRadius = 0;
    private double deltaPos = 0;

    private CustomState interpolate(CustomState endVal, double t) {
        CustomState lerpedState = new CustomState();

        lerpedState.timeSeconds = doubleLerp(timeSeconds, endVal.timeSeconds, t);
        double deltaT = lerpedState.timeSeconds - timeSeconds;

        if (deltaT < 0) {
            return endVal.interpolate(this, 1 - t);
        }

        lerpedState.velocityMetersPerSecond =
                doubleLerp(velocityMetersPerSecond, endVal.velocityMetersPerSecond, t);
        lerpedState.accelerationMetersPerSecondSq =
                doubleLerp(
                        accelerationMetersPerSecondSq, endVal.accelerationMetersPerSecondSq, t);
        Translation2d newTrans =
                translationLerp(
                        poseMeters.getTranslation(), endVal.poseMeters.getTranslation(), t);
        Rotation2d newHeading =
                rotationLerp(poseMeters.getRotation(), endVal.poseMeters.getRotation(), t);
        lerpedState.poseMeters = new Pose2d(newTrans, newHeading);
        lerpedState.angularVelocityRadPerSec =
                doubleLerp(angularVelocityRadPerSec, endVal.angularVelocityRadPerSec, t);
        lerpedState.holonomicAngularVelocityRadPerSec =
                doubleLerp(
                        holonomicAngularVelocityRadPerSec, endVal.holonomicAngularVelocityRadPerSec, t);
        lerpedState.holonomicRotation =
                rotationLerp(holonomicRotation, endVal.holonomicRotation, t);
        lerpedState.curveRadius = doubleLerp(curveRadius, endVal.curveRadius, t);
        lerpedState.curvatureRadPerMeter =
                doubleLerp(curvatureRadPerMeter, endVal.curvatureRadPerMeter, t);

        return lerpedState;
    }

}
