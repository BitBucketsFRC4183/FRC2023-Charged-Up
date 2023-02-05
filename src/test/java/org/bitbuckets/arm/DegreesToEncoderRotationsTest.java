package org.bitbuckets.arm;

import org.bitbuckets.lib.hardware.IMotorController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DegreesToEncoderRotationsTest {

    final IMotorController lowerJoint;
    final IMotorController upperJoint;

    public DegreesToEncoderRotationsTest(IMotorController lowerJoint, IMotorController upperJoint) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;
    }

    @Test
    public void name() {
        assertEquals(0, new ArmControl(lowerJoint, upperJoint, null, null, null, null, null).convertMechanismRotationtoRawRotation_lowerJoint(180));
    }
}
