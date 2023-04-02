package org.bitbuckets.rgb;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleConfiguration;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.util.MockingUtil;

public class RgbSubsystemSetup implements ISetup<RgbSubsystem> {

    final OperatorInput operatorInput;
    final boolean isEnabled;

    public RgbSubsystemSetup(OperatorInput operatorInput, boolean isEnabled) {
        this.operatorInput = operatorInput;
        this.isEnabled = isEnabled;
    }


    @Override
    public RgbSubsystem build(IProcess self) {
        if (!isEnabled) {
            return MockingUtil.buddy(RgbSubsystem.class);
        }


        CANdle candle = new CANdle(15);
        IDebuggable debuggable = self.getDebuggable();

        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = CANdle.LEDStripType.RGB; // set the strip type to RGB

        config.brightnessScalar = 1; // dim the LEDs to half brightness
        candle.configAllSettings(config);

        return new RgbSubsystem(candle, operatorInput, debuggable, operatorInput);
    }
}
