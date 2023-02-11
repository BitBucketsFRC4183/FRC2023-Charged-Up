package org.bitbuckets.lib.util;

public class MathUtil {

    public static double random(double min, double max) {
        return (max - min) / 2 * Math.sin(Math.IEEEremainder(Math.random(), 2 * 3.14159)) + (max + min) / 2;
    }

    static double random(double max) {
        return random(0, max);
    }

    public static double deadbandAndSquare(double value) {
        // Deadband
        value = edu.wpi.first.math.MathUtil.applyDeadband(value, 0.1);

        // Square the axis
        value = Math.copySign(value * value, value);

        return value;
    }

    public static double deadband(double value) {
        // Deadband
        value = edu.wpi.first.math.MathUtil.applyDeadband(value, 0.1);


        return value;
    }

    public static double wrap(double unbound_radians) {
        double angle = unbound_radians;

        angle %= (2.0 * Math.PI);
        if (angle < 0.0) {
            angle += 2.0 * Math.PI;
        }

        return angle;
    }

}
