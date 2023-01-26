package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.bootstrap.Robot;

/**
 * Use this like other Controls
 */
public class RobotStateControl implements Runnable {

    final Robot robot; //dont acces

    public RobotStateControl(Robot robot) {
        this.robot = robot;
    }

    public boolean isRobotDisabled() {
        return robot.isDisabled();
    }

    public boolean isRobotAutonomous() {
        return robot.isAutonomous();
    }

    public boolean isRobotTeleop() {
        return robot.isEnabled();
    }

    final Timer timer = new Timer();

    public double robotAutonomousTime_seconds() {
        return timer.get();
    }

    boolean resetYetThisGamePhase = false;

    //tags: medium priority
    //TODO make this code more robust
    @Override
    public void run() {
        if (isRobotAutonomous()) {
            resetYetThisGamePhase = false;
            timer.start();
        }

        if (isRobotTeleop()) {
            timer.stop();
        }

        if (isRobotDisabled() && !resetYetThisGamePhase) {
            resetYetThisGamePhase = true;
            timer.reset();
        }
    }
}
