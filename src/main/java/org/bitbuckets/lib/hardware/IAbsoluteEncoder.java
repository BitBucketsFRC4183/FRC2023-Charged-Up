package org.bitbuckets.lib.hardware;

@FunctionalInterface
public interface IAbsoluteEncoder {
    /**
     * Gets the current angle reading of the encoder in radians.
     *
     * @return The current angle in radians. Range: [0, 2pi)
     */
    double getAbsoluteAngle() {

    }

    ;
}
