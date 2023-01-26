package org.bitbuckets.drive.controlsds;

import com.ctre.phoenix.sensors.CANCoder;
import org.bitbuckets.drive.controlsds.sds.AbsoluteEncoder;

public class CANCoderAbsoluteEncoder implements AbsoluteEncoder {
    private final CANCoder encoder;

    public CANCoderAbsoluteEncoder(CANCoder encoder) {
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

