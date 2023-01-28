package org.bitbuckets.lib.hardware;

import org.bitbuckets.lib.util.AngleUtil;

/**
 * This represents something that is or has an encoder.
 * Encoders count movement of something (usually an axis)
 */
public interface IEncoder extends IRaw {

    //actual methods

    /**
     *
     * @return a coefficient representing the conversion of rotations of the motor
     * to rotations of whatever mechanism the motor is meant to be driving
     */
    double getMechanismFactor();

    /**
     *
     * @return a coefficient which converts from rotations of the motor to meters in position,
     * only used for velocity based PID or getting velocity readouts.
     */
    double getRotationsToMetersFactor();

    /**
     *
     * @return a coefficient which converts from whatever the raw units of the encoder are (sensor units for ctre)
     * to rotations.
     */
    double getRawToRotationsFactor();

    /**
     *
     * @return a coefficient which converts from whatever time unit^-1 the encoder uses to seconds^-1
     */
    double getTimeFactor();

    /**
     *
     * @return The raw position output of the encoder
     */
    double getPositionRaw();

    /**
     *
     * @return The raw velocity output of the encoder
     */
    double getVelocityRaw();

    /**
     * forces the encoder's internal brain's reading to a specific quantity of units
     * Useful for zeroing an encoder or making sure swerve offsets are correct
     *
     * @param offsetUnits_baseUnits
     */
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
