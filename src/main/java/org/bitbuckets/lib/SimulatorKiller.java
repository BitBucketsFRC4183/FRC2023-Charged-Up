package org.bitbuckets.lib;

import org.bitbuckets.lib.util.HasLoop;

/**
 * Designed to kill the robot
 */
public class SimulatorKiller implements HasLoop {

    //only should be registered if..

    int counter = 0;

    @Override
    public void loop() {
        if (++counter > 700) {
            //If running in sim and it can run longer than 15 seconds, pass the build
            System.exit(0);
        }
    }
}
