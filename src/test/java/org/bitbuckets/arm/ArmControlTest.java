package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void convertMechanismRotationToRawRotation_lowerJoint() {
        ArmControl control = new ArmControl(lowerJoint, upperJoint);

        assertEquals(27.69, control.convertMechanismRotationtoRawRotation_lowerJoint(1), .1);
    }

    @Test
    void convertMechanismRotationToRawRotation_upperJoint() {
        ArmControl control = new ArmControl(lowerJoint, upperJoint);

        assertEquals(24., control.convertMechanismRotationtoRawRotation_upperJoint(1), .1);
    }
}