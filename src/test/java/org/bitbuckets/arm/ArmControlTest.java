package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ArmControlTest {

    IMotorController lowerJoint;
    IMotorController upperJoint;

    @BeforeEach
    public void beforeEach() {
        lowerJoint = mock();
        upperJoint = mock();
    }

    @Disabled
    @Test
    void convertMechanismRotationToRawRotation_lowerJoint() {
        ArmControl control = new ArmControl(lowerJoint, upperJoint, Mockito.mock());

        assertEquals(27.69, control.convertMechanismRotationtoRawRotation_lowerJoint(1), .1);
    }

    @Disabled
    @Test
    void convertMechanismRotationToRawRotation_upperJoint() {
        ArmControl control = new ArmControl(lowerJoint, upperJoint, Mockito.mock());

        assertEquals(24., control.convertMechanismRotationtoRawRotation_upperJoint(1), .1);
    }
}