package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.ILogAs;
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
        
        ILoggable<Double> raw = self.generateLogger(ILogAs.DOUBLE, "raw-rad" + Math.random());
        ILoggable<Double> abs = self.generateLogger(ILogAs.DOUBLE, "abs-rad" + Math.random());
        ILoggable<Double> volt = self.generateLogger(ILogAs.DOUBLE, "voltage" + Math.random());

        return new ThriftyEncoder(input, offset_radians, raw, abs, volt);
    }

}
