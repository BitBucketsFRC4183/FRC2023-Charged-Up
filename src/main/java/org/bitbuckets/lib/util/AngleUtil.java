package org.bitbuckets.lib.util;

public class AngleUtil {

    /**
     * constrains a parameter to the range [0, 2pi]
     *
     * @param unbound_radians any radian value
     * @return that radian value represented from [0 to 2pi]
     */
    public static double wrap(double unbound_radians) {
        double angle = unbound_radians;

        angle %= (2.0 * Math.PI);
        if (angle < 0.0) {
            angle += 2.0 * Math.PI;
        }

        return angle;
    }

    public static double wrapRotations(double unbound_rotationsInitial) {
        double unbound_rotations = unbound_rotationsInitial;

        if (unbound_rotations > 1.0) {

            while (unbound_rotations > 1.0) {
                unbound_rotations -= 1.0;
            }
        }

        if (unbound_rotations < 0) {
            while (unbound_rotations < 0) {
                unbound_rotations += 1.0;
            }
        }

        return unbound_rotations;
    }

}
