package org.bitbuckets.lib.vendor.thrifty;

import edu.wpi.first.wpilibj.AnalogInput;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.SetupProfiler;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

public class ThriftyEncoderSetup implements ISetup<IAbsoluteEncoder> {

    final int channel;

    public ThriftyEncoderSetup(int channel) {
        this.channel = channel;
    }

    @Override
    public IAbsoluteEncoder build(ProcessPath path) {

        SetupProfiler libSetup = path.generateSetupProfiler("lib-setup");
        AnalogInput input = new AnalogInput(channel);

        return new ThriftyEncoder(input);
    }

}
