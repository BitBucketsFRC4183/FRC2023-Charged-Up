package org.bitbuckets.lib.DIO;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;

public class DIOEncoderSetup implements ISetup<IAbsoluteEncoder> {

    final int channel;


    public DIOEncoderSetup(int channel) {
        this.channel = channel;
    }

    @Override
    public IAbsoluteEncoder build(IProcess self) {

        DutyCycleEncoder encoder = new DutyCycleEncoder(this.channel);

//        ILoggable<Double> raw = self.generateLogger(ILogAs.DOUBLE, "raw-deg" + Math.random());
//        ILoggable<Double> abs = self.generateLogger(ILogAs.DOUBLE, "abs-deg" + Math.random());
//        ILoggable<Double> volt = self.generateLogger(ILogAs.DOUBLE, "voltage" + Math.random());

        return new DIOEncoder(encoder);
    }

}
