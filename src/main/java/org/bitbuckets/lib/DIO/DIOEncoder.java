package org.bitbuckets.lib.DIO;

import edu.wpi.first.wpilibj.DutyCycleEncoder;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

public class DIOEncoder implements IAbsoluteEncoder, HasLogLoop {

    final DutyCycleEncoder dutyCycleEncoder;

    public DIOEncoder(DutyCycleEncoder dutyCycleEncoder) {
        this.dutyCycleEncoder = dutyCycleEncoder;
    }

    @Override
    public double getAbsoluteAngle() {
        double angle = Math.toRadians(dutyCycleEncoder.getAbsolutePosition());
        angle %= 2.0 * Math.PI;
        if (angle < 0.0) {
            angle += 2.0 * Math.PI;
        }

        return angle;

    }

    @Override
    public void logLoop() {

    }
}
