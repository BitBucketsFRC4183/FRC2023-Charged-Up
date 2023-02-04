package org.bitbuckets.auto;

import com.pathplanner.lib.PathPlannerTrajectory;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.arm.ArmControl;

public class AutoControl {


    //PathPlannerTrajectory is an array so it can call from the different paths in the enum
    private final PathPlannerTrajectory[] trajectory;
    private final HolonomicDriveController controller;

    final ArmControl armControl;

    //creates paths and Holonomic Controller (used in PathPlanner and to get the code to run the path)
    public AutoControl(PathPlannerTrajectory[] trajectory, HolonomicDriveController controller, ArmControl armControl) {
        this.trajectory = trajectory;
        this.controller = controller;
        this.armControl = armControl;
    }

    public ChassisSpeeds getAutoChassisSpeeds(AutoPath path, double time, Pose2d pose) {

        var desiredState = (PathPlannerTrajectory.PathPlannerState) trajectory[path.index].sample(time);

        return controller.calculate(pose, desiredState, desiredState.holonomicRotation);
    }

    public double getTrajectoryTime(AutoPath path) {
        return trajectory[path.index].getTotalTimeSeconds();

    }

    Timer eventTimer = new Timer();

    public void setMarkers() {

        if (eventTimer.get() > trajectory[2].getMarkers().get(2).timeSeconds) {
            //armControl.moveUpperArm(0.1);
            //get arm system to work
        }
    }

}
