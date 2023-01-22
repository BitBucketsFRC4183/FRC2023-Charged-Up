package org.bitbuckets.drive.fenc;

import org.bitbuckets.lib.hardware.IEncoder;

/**
 * TODO: this needs a specific optimization where if the drive desired angle
 * vs current angle is greater than pi we can reverse the drive direction instead
 */
public class OptimizeEncoderWrapper {


    public final IEncoder relative;

    public OptimizeEncoderWrapper(IEncoder relative) {
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


    //can i find l
    public double optimizeSetpointWithMechanismRads_encoderRads(double setpoint_mechanismRads) {
        double goal = setpoint_mechanismRads / relative.getMechanismFactor();

        return optimizeSetpoint_encoderRads(goal);
    }

    public double optimizeSetpoint_encoderRads(double setpoint_encoderRads) {
        double accumulated = relative.getEncoderPositionAccumulated_radians();

        System.out.println(setpoint_encoderRads + "is EncoderRads, " + accumulated + " is accumulated");

        return optimizeWithBoth(setpoint_encoderRads, accumulated);
    }

}
