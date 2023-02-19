package org.bitbuckets.RGB;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleConfiguration;
import edu.wpi.first.wpilibj.Joystick;
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


        CANdle candle = new CANdle(15);
        Debuggable debuggable = self.generateDebugger();
        RgbInput rgbInput = new RgbInput(new Joystick(1));

        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = CANdle.LEDStripType.RGB; // set the strip type to RGB

        config.brightnessScalar = 1; // dim the LEDs to half brightness
        candle.configAllSettings(config);

        return new RgbSubsystem(candle, rgbInput, debuggable);
    }
}
