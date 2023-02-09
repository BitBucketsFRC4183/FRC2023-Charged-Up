package org.bitbuckets.lib.util;

public interface HasLogicLoop extends Runnable {

    @Override
    default void run() {
       logicLoop();
    }

    /**
     * Method that does something to the robot
     */
    void logicLoop();
}
