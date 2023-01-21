package org.bitbuckets.lib.util;

public class MathUtil {

    public static double random(double min, double max) {
        return (max - min) / 2 * Math.sin(Math.IEEEremainder(Math.random(), 2 * 3.14159)) + (max + min) / 2;
    }
    static double random(double max) {
        return random(0, max);
    }

}
