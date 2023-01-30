package org.bitbuckets.lib.hardware;

/**
 * This class represents something that has or is an absolute encoder
 * which is used to get an angle even after the robot restarts
 */
@FunctionalInterface
public interface IAbsoluteEncoder {
    /**
     * Gets the current angle reading of the encoder in radians.
     *
     * @return The current angle in radians. Range: [0, 2pi)
     */
    double getAbsoluteAngle();
}
