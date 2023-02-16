package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;

public class ThriftyEncoderSetup implements ISetup<IAbsoluteEncoder> {

    final int channel;

    final double offset_radians;

    public ThriftyEncoderSetup(int channel, double offset_radians) {
        this.channel = channel;
        this.offset_radians = offset_radians;
    }

    @Override
    public IAbsoluteEncoder build(IProcess self) {

        AnalogInput input = new AnalogInput(channel);
        
        ILoggable<Double> raw = self.generateLogger(Double.class, "raw-rads");
        ILoggable<Double> abs = self.generateLogger(Double.class, "abs-rads");
        ILoggable<Double> volt = self.generateLogger(Double.class, "voltage");

        return new ThriftyEncoder(input, offset_radians, raw, abs, volt);
    }

}
