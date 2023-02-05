package org.bitbuckets.elevator;

public class ElevatorConstants {
    public static final double chainRatioTilt = 64.0 / 10.0;

    public static final double gearRatioTilt = 1 / 15.1147;

    public static final double finalGearTilt = chainRatioTilt * gearRatioTilt;
    public static final double rotToMeterExtend = 1.0;
    public static final double rotToMeterTilt = 1.0;

    public static final double getGearRatioExtend = 1 / 5.5 / 15;// rotations of motor to mechanism  22*0.25 = 5.5

    public ElevatorConstants() {

    }

}
