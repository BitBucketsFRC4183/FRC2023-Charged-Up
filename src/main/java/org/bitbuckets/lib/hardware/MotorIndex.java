package org.bitbuckets.lib.hardware;

public enum MotorIndex {

    ;

    public static final byte MECHANISM_FACTOR = 0;
    public static final byte ROTATION_TO_METER_FACTOR = 1;
    public static final byte TIME_FACTOR = 2;
    public static final byte INVERTED = 3;

    public static double[] CONSTANTS(double mechanismFactor, double rotationToMeterFactor, double timeFactor, boolean inverted) {
        return new double[] {
                mechanismFactor,
                rotationToMeterFactor,
                timeFactor,
                inverted ? 1 : 0 //waste of bytes but who cares
        };
    }
}
