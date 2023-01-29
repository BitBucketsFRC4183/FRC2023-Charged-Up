package org.bitbuckets.lib.hardware;

/**
 * Index for pid constants to be used on an array storing pid constants
 * <p>
 * THIS IS A PLACEHOLDER UNTIL TUNEABELS CAN BE MERGED
 */
@Deprecated
public enum PIDIndex {

    ;

    /**
     * This index represents the proportional constant. Increase that constant first.
     */
    public final static byte P = 0;

    /**
     * This index represents the integral constant. Don't use it unless you have to.
     */
    public final static byte I = 1;

    /**
     * This index represents the derivative (rate of change) constant. use it to correct
     * overshoot (lets the pid algorithm "predict" if the current rate of change will
     * overshoot)
     */
    public final static byte D = 2;

    /**
     * This is the feedforward constant's index. The feedforward constant should be used
     * if there is a steady state effector like gravity that you can cancel out with a known constant.
     */
    public final static byte FF = 3;

    /**
     * TODO integral zone explanation
     */
    public final static byte IZONE = 4;

    @Deprecated
    public static double[] CONSTANTS(double p, double i, double d, double ff, double izone) {
        return new double[]{
                p,
                i,
                d,
                ff,
                izone
        };
    }

    @Deprecated
    public static final double[] EMPTY = new double[5];

}
