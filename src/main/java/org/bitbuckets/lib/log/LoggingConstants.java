package org.bitbuckets.lib.log;

/**
 * Constants used when registering runnables that specifically represent logging loops.
 * It is best practice to use these instead of hardcoding values but if they are not sufficient
 * you can use your own
 */
public interface LoggingConstants {

    //log data every 100ms
    int LOGGING_PERIOD = 100;

    //update tune values from the smartdashboard every half second
    int TUNING_PERIOD = 500;

}
