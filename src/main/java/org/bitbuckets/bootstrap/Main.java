package org.bitbuckets.bootstrap;

import edu.wpi.first.wpilibj.RobotBase;
import org.bitbuckets.RobotSetup;

public class Main {


    public static void main(String[] args) {
        RobotBase.startRobot(() -> {
            try {
                new Robot(new RobotSetup(robot));
            } catch (Exception e) {
                throw new IllegalStateException(e);
                //mattlib exception handling goes here!
            }
        });
    }
}
