package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.WPI_CANCoder;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

public class CANCoderAbsoluteEncoderSetup implements ISetup<IAbsoluteEncoder> {
    final int canId;

    public CANCoderAbsoluteEncoderSetup(int canId) {
        this.canId = canId;
    }

    @Override
    public IAbsoluteEncoder build(ProcessPath path) {
        WPI_CANCoder encoder = new WPI_CANCoder(canId);
        return new CANCoderAbsoluteEncoder(encoder);
    }
}
