package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.WPI_CANCoder;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;

public class CANCoderAbsolute implements IAbsoluteEncoder, HasLogLoop {
    final WPI_CANCoder encoder;

    final ILoggable<Double> absolutePos;
    final ILoggable<Double> absoluteAngle;

    public CANCoderAbsolute(WPI_CANCoder encoder, ILoggable<Double> absolutePos, ILoggable<Double> absoluteAngle) {
        this.encoder = encoder;
        this.absolutePos = absolutePos;
        this.absoluteAngle = absoluteAngle;
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
    public void logLoop() {
        absolutePos.log(encoder.getAbsolutePosition());
        absoluteAngle.log(getAbsoluteAngle());
    }
}
