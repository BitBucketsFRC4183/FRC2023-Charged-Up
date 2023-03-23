package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;


/**
 * It works now don't touch it
 */
public class ThriftyEncoder implements IAbsoluteEncoder, HasLogLoop {

    private static final double READ_VOLTAGE_MAX = 4.8;
    final AnalogInput input;
    final double offset_radians;

    final ILoggable<Double> rawPosition;
    final ILoggable<Double> absoluteAngle;
    final ILoggable<Double> voltage;


    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, double offset_radians, ILoggable<Double> rawPosition, ILoggable<Double> absoluteAngle, ILoggable<Double> voltage) {
        this.input = input;
        this.offset_radians = offset_radians;
        this.rawPosition = rawPosition;
        this.absoluteAngle = absoluteAngle;
        this.voltage = voltage;
    }

    // get position
    public double getPositionRadians() {
        return this.getRawPositionRadians() - offset_radians;
    }

    public double getRawPositionRadians() {
        return (input.getVoltage() * 2 * Math.PI) / READ_VOLTAGE_MAX;
    }

    @Override
    public double getAbsoluteAngle() {
        return getPositionRadians();
    }



    @Override
    public void logLoop() {
        rawPosition.log(Math.toDegrees(getRawPositionRadians()));
        absoluteAngle.log(Math.toDegrees(getAbsoluteAngle()));
        voltage.log(input.getVoltage());
    }
}