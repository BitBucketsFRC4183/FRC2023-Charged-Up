package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ArmControlTest {


    @Disabled
    @Test
    void convertMechanismRotationToRawRotation_lowerJoint() {
        ArmControl control = new ArmControl(Mockito.mock(), Mockito.mock(), Mockito.mock());

        assertEquals(27.69, control.convertMechanismRotationtoRawRotation_lowerJoint(1), .1);
    }

    @Disabled
    @Test
    void convertMechanismRotationToRawRotation_upperJoint() {
        ArmControl control = new ArmControl(Mockito.mock(), Mockito.mock(), Mockito.mock());

        assertEquals(24., control.convertMechanismRotationtoRawRotation_upperJoint(1), .1);
    }



    @Test
    void test() {
        IMotorController lowerJointSpy = Mockito.mock();

        ArmControl control = new ArmControl(lowerJointSpy, Mockito.mock(), Mockito.mock());
        control.moveToDegrees(90, 0);


        Mockito.verify(lowerJointSpy).moveToPosition(12.0);

    }
}