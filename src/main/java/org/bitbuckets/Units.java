package org.bitbuckets;

public class Units {

    public static double toSensorUnits(double revolutions) {
        return revolutions * 2048.0;
    }


}
