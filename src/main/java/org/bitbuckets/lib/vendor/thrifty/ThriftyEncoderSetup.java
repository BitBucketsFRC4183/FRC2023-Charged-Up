package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.StartupProfiler;
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
    public IAbsoluteEncoder build(ProcessPath path) {

        StartupProfiler libSetup = path.generateSetupProfiler("lib-setup");
        AnalogInput input = new AnalogInput(channel);
        
        ILoggable<double[]> thriftydata = path.generateDoubleLoggers("Raw-Angle-Degrees", "Angle-Degrees", "Voltage");
        ThriftyEncoder thrifty = new ThriftyEncoder(input, thriftydata, offset_radians);

        path.registerLoop(thrifty, 100, "thrifty-log-loop");

        return thrifty;
    }

}
