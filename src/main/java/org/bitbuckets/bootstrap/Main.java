package org.bitbuckets.bootstrap;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import org.bitbuckets.RobotSetup;

public class Main {


    static class nut extends TimedRobot {

    }

    public static void main(String[] args) {
        RobotBase.startRobot(() -> new Robot(new RobotSetup()));
    }
}
