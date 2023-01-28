package org.bitbuckets.lib.hardware;

public enum MotorIndex {

    ;

    public static final byte MECHANISM_FACTOR = 0;
    public static final byte ROTATION_TO_METER_FACTOR = 1;
    public static final byte TIME_FACTOR = 2;
    public static final byte INVERTED = 3;
    public static final byte CURRENT_LIMIT = 4;
    public static final byte IS_BRAKE = 5; //Will be neutral otherwise

    /**
     *
     * @param mechanismFactor factor that convert to mechanism rotations
     * @param rotationToMeterFactor factor that converts rotations to pure position
     * @param timeFactor factor that converts from a native timestep (units of time^-1) to seconds
     * @param inverted true if the motor is inverted
     * @param currentLimit a limit of the current
     * @param isBreak true if the motor should use breaking when idle (resist changes by flipping current), false if motor should neutral when idle (do nothing)
     * @return constants array
     */
    public static double[] CONSTANTS(double mechanismFactor, double rotationToMeterFactor, double timeFactor, boolean inverted, double currentLimit, boolean isBreak) {
        return new double[] {
                mechanismFactor,
                rotationToMeterFactor,
                timeFactor,
                inverted ? 1 : 0, //waste of bytes but who cares
                currentLimit,
                isBreak ? 1 : 0
        };
    }
}
