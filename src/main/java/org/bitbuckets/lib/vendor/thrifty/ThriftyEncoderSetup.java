package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.StartupLogger;

public class ThriftyEncoderSetup implements ISetup<IAbsoluteEncoder> {

    final int channel;

    public ThriftyEncoderSetup(int channel) {
        this.channel = channel;
    }

    @Override
    public IAbsoluteEncoder build(ProcessPath path) {

        //this was throwing errors when i was trying to build
        StartupLogger libSetup = path.generateStartupLogger("lib-setup");
        AnalogInput input = new AnalogInput(channel);

        return new ThriftyEncoder(input);
    }

}
