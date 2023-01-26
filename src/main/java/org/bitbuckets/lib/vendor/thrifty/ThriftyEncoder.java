package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

public class ThriftyEncoder implements IAbsoluteEncoder {


    final AnalogInput input;

    public ThriftyEncoder(AnalogInput input) {
        this.input = input;
    }

    @Override
    public double getAbsoluteAngle() {
        return (input.getVoltage() / 5) * 2 * Math.PI;
    }
}
