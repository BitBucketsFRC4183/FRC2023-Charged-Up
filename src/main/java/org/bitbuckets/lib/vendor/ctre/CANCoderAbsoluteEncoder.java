package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.WPI_CANCoder;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;

public class CANCoderAbsoluteEncoder implements IAbsoluteEncoder, Runnable {
    final WPI_CANCoder encoder;

    final ILoggable<double[]> encoderData;

    CANCoderAbsoluteEncoder(WPI_CANCoder encoder, ILoggable<double[]> encoderData) {
        this.encoder = encoder;
        this.encoderData = encoderData;
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

    @Override
    public void run() {
        encoderData.log(new double[]{
                encoder.getAbsolutePosition(),
                Math.toDegrees(getAbsoluteAngle())
        });

    }
}
