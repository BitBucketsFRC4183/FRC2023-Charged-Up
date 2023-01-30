package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;


public class ThriftyEncoder implements IAbsoluteEncoder, Runnable {

    private static final double READ_VOLTAGE_MAX = 4.8;
    final AnalogInput input;
    final ILoggable<double[]> allThriftyEncoderData;
    final double offset_radians;


    // read voltage, get input
    public ThriftyEncoder(AnalogInput input, ILoggable<double[]> allThriftyEncoderData, double offset_radians) {
        this.input = input;
        this.allThriftyEncoderData = allThriftyEncoderData;
        this.offset_radians = offset_radians;
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
    public void run() {
        allThriftyEncoderData.log(new double[] {
                Math.toDegrees(getRawPositionRadians()),
                Math.toDegrees(getAbsoluteAngle()),
                input.getVoltage(),
        });
    }
}