package org.bitbuckets.lib.encoder;

import org.bitbuckets.drive.fenc.OptimizeEncoderWrapper;
import org.bitbuckets.lib.hardware.IEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

//im so tired of can busses cant we just have like
class OptimizeEncoderWrapperTest {


    @Test
    void calculateOptimalSetpointRadiansForward() {
        IEncoder encoder = Mockito.mock(IEncoder.class);
        Mockito.when(encoder.getMechanismFactor()).thenReturn(1.0);
        Mockito.when(encoder.getEncoderPositionAccumulated_radians()).thenReturn(Math.PI * 2.0);

        OptimizeEncoderWrapper optimizeEncoderWrapper = new OptimizeEncoderWrapper(encoder);
        double setpoint = optimizeEncoderWrapper.optimizeSetpointWithMechanismRads_encoderRads(0.5 * Math.PI);

        Assertions.assertEquals(2.5 * Math.PI, setpoint);

    }

    @Test
    void calculateOptimalSetpointRadiansForwardOverShoot() {
        IEncoder encoder = Mockito.mock(IEncoder.class);
        Mockito.when(encoder.getMechanismFactor()).thenReturn(1.0);
        Mockito.when(encoder.getEncoderPositionAccumulated_radians()).thenReturn(Math.PI * 2.0);

        OptimizeEncoderWrapper optimizeEncoderWrapper = new OptimizeEncoderWrapper(encoder);
        double setpoint = optimizeEncoderWrapper.optimizeSetpointWithMechanismRads_encoderRads(20 * Math.PI);

        Assertions.assertEquals(Math.PI * 2, setpoint, .1);

    }


    @Test
    void calculateOptimalSetpointRadiansBackwards() {
        IEncoder encoder = Mockito.mock(IEncoder.class);
        Mockito.when(encoder.getMechanismFactor()).thenReturn(1.0);
        Mockito.when(encoder.getEncoderPositionAccumulated_radians()).thenReturn(Math.PI * 4.0);

        OptimizeEncoderWrapper optimizeEncoderWrapper = new OptimizeEncoderWrapper(encoder);
        double setpoint = optimizeEncoderWrapper.optimizeSetpointWithMechanismRads_encoderRads(1.5 * Math.PI);

        Assertions.assertEquals(3.5 * Math.PI, setpoint, 0.0001);

    }


    @Test
    void calculateOptimalSetpoint_shouldBeIntelligent() {
        IEncoder encoder = Mockito.mock(IEncoder.class);
        Mockito.when(encoder.getMechanismFactor()).thenReturn(0.5);
        Mockito.when(encoder.getEncoderPositionAccumulated_radians()).thenReturn(Math.PI * 4.0);

        OptimizeEncoderWrapper optimizeEncoderWrapper = new OptimizeEncoderWrapper(encoder);

        double setpointMechrads = 0.5 * Math.PI; //why does this fix the code?? im so confused
        double setpoint = optimizeEncoderWrapper.optimizeSetpointWithMechanismRads_encoderRads(setpointMechrads);


        Assertions.assertEquals(5.0 * Math.PI, setpoint, 0.0001);
        //if we are at 2pi axis, we want to move 0.5pi axis, and 1pi encoder translates to 0.5 axis, the most optimal move
        //is to move to 3pi OR 5pi encoder minimizing rotations. In this case it goes to 5pi because it prioritizese increasing
        //encoder count. TODO should we prioritize reducing encoder count? this can help counteract the gradual encoder buildup
        //but will also possibly cause negatives (death)

    }

    @Test
    void calculateOptimalSetpoint_shouldGoBackwards() {
        IEncoder encoder = Mockito.mock(IEncoder.class);
        Mockito.when(encoder.getMechanismFactor()).thenReturn(0.5);
        Mockito.when(encoder.getEncoderPositionAccumulated_radians()).thenReturn(Math.PI * 0.25);

        OptimizeEncoderWrapper optimizeEncoderWrapper = new OptimizeEncoderWrapper(encoder);

        double setpoint = optimizeEncoderWrapper.optimizeSetpointWithMechanismRads_encoderRads(0.25 * Math.PI); //we want to move 1/2 pi encoder radians
        //This should move the motor backwards
        Assertions.assertEquals(0.5 * Math.PI, setpoint, 0.001);
    }
}