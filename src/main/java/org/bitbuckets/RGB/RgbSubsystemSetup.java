package org.bitbuckets.RGB;

import com.ctre.phoenix.led.CANdle;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;

public class RgbSubsystemSetup implements ISetup<RgbSubsystem> {

    final boolean isEnabled;

    public RgbSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }


    @Override
    public RgbSubsystem build(ProcessPath self) {
        if (!isEnabled) {
            return MockingUtil.buddy(RgbSubsystem.class);
        }

        CANdle candle =  new CANdle(15);
        Debuggable debuggable = self.generateDebugger();

        return new RgbSubsystem(candle, debuggable);
    }
}
