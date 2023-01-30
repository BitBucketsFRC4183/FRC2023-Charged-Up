package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.WPI_CANCoder;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

public class CANCoderAbsoluteEncoder implements IAbsoluteEncoder {
    final WPI_CANCoder encoder;

    CANCoderAbsoluteEncoder(WPI_CANCoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public double getAbsoluteAngle() {
        double angle = Math.toRadians(encoder.getAbsolutePosition());
        angle %= 2.0 * Math.PI;
        if (angle < 0.0) {
            angle += 2.0 * Math.PI;
        }

        return angle;

    }
}
