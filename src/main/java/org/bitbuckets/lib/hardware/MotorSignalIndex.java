package org.bitbuckets.lib.hardware;

/**
 * helpful debugging tips: graph moved_position and moved_percent, if both are being called
 * at the same time your code sucks
 */
public enum MotorSignalIndex {
    ;

    /**
     * This signal is set to true only on the tick when the force output method is called
     */
    final static byte FORCED_OFFSET = 0;

    /**
     * This signal is set to true only on the tick(s) when control using percent output is called
     */
    final static byte MOVED_PERCENT = 1;

    /**
     * This signal is set to true on ticks when position control is used
     */
    final static byte MOVED_POSITION = 2;

    /**
     * This signal is set to true on ticks when velocity control is used
     */
    final static byte MOVED_VELOCITY = 3;

    /**
     * IMPORTANT:  this signal is set to true if the hardware controller reboots during a match
     * If this signal is turning on at all something horrible is happening and you should check your basement.
     */
    final static byte SHUTDOWN_OCCURRED = 4;

}
