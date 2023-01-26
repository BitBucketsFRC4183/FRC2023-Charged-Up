package org.bitbuckets.lib.hardware;

import org.bitbuckets.lib.util.AngleUtil;

/**
 * Represents an encoder that's on a motor controller
 * it's being read from the motor controller and not a DIO/analog port on the RIO
 */
public interface IEncoder extends IRaw {

    //actual methods

    double getMechanismFactor();

    double getRotationsToMetersFactor();

    double getRawToRotationsFactor();

    double getTimeFactor();

    double getPositionRaw();

    double getVelocityRaw();


    @Deprecated
        //DONT USE THIS UNLESS YOU HAVE TO
    void forceOffset(double offsetUnits_baseUnits);


    //utility methods


    default double getEncoderPositionAccumulated_radians() {
        return getPositionRaw() * getRawToRotationsFactor() * Math.PI * 2.0;
    }

    default double getEncoderPositionBounded_radians() {
        return AngleUtil.wrap(getEncoderPositionAccumulated_radians());
    }

    default double getMechanismPositionAccumulated_radians() {
        return getEncoderPositionAccumulated_radians() * getMechanismFactor();
    }

    default double getMechanismPositionBounded_radians() {
        return AngleUtil.wrap(getMechanismPositionAccumulated_radians());
    }


    default double getPositionMechanism_meters() {
        return getPositionEncoder_meters() * getMechanismFactor();
    }

    default double getPositionEncoder_meters() {
        return getPositionRaw() * getRawToRotationsFactor() * getRotationsToMetersFactor();
    }

    default double getVelocityMechanism_metersPerSecond() {
        return getVelocityEncoder_metersPerSecond() * getMechanismFactor();
    }

    default double getVelocityEncoder_metersPerSecond() {
        return getVelocityRaw() * getRawToRotationsFactor() * getRotationsToMetersFactor() * getTimeFactor();
    }


}
