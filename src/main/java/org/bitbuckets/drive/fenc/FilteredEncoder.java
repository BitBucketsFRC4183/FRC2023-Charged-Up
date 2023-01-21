package org.bitbuckets.drive.fenc;

import org.bitbuckets.lib.hardware.IEncoder;

public class FilteredEncoder {


    public final IEncoder relative;

    public FilteredEncoder(IEncoder relative) {
        this.relative = relative;
    }
    double CIRCLE = Math.PI * 2.0;
    double HALF_CIRCLE = Math.PI;

    private double optimizeWithBoth(double setpoint_encoderRads, double current_encoderRads) {
        double lowerBound;
        double upperBound;
        double lowerOffset = current_encoderRads % CIRCLE;

        //TODO this breaks when lowerOffset is exactly 0 (it chooses to wrap around)
        if (lowerOffset >= 0) {
            lowerBound = current_encoderRads - lowerOffset;
            upperBound = current_encoderRads + (CIRCLE - lowerOffset);
        } else {
            upperBound = current_encoderRads - lowerOffset;
            lowerBound = current_encoderRads - (CIRCLE + lowerOffset);
        }
        while (setpoint_encoderRads < lowerBound) {
            setpoint_encoderRads += CIRCLE;
        }
        while (setpoint_encoderRads > upperBound) {
            setpoint_encoderRads -= CIRCLE;
        }
        if (setpoint_encoderRads - current_encoderRads > HALF_CIRCLE) {
            setpoint_encoderRads -= CIRCLE;
        } else if (setpoint_encoderRads - current_encoderRads < -HALF_CIRCLE) {
            setpoint_encoderRads += CIRCLE;
        };
        return setpoint_encoderRads;
    }


    public double optimizeSetpointWithMechanismRads_encoderRads(double setpoint_mechanismRads) {
        double goal = setpoint_mechanismRads / relative.getMechanismFactor();

        return optimizeSetpoint_encoderRads(goal);
    }

    public double optimizeSetpoint_encoderRads(double setpoint_encoderRads) {
        double accumulated = relative.getEncoderPositionAccumulated_radians()
                ;

        return optimizeWithBoth(setpoint_encoderRads, accumulated);
    }

}
