package org.bitbuckets.robot;

/**
 * Designed to kill the robot
 */
public class SimulatorKillAspect implements Runnable {

    //only should be registered if..

    int counter = 0;

    @Override
    public void run() {
        if (++counter > 700) {
            //If running in sim and it can run longer than 15 seconds, pass the build
            System.exit(0);
        }
    }
}
