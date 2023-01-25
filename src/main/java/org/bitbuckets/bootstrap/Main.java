package org.bitbuckets.bootstrap;

import edu.wpi.first.wpilibj.RobotBase;
import org.bitbuckets.robot.RobotSetup;

public class Main {


    public static void main(String[] args) {
        RobotBase.startRobot(() -> new Robot(new RobotSetup()));
    }
}
