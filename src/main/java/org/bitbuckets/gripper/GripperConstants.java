package org.bitbuckets.gripper;

import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;

import java.util.Optional;

public interface GripperConstants {

    MotorConfig GRIPPER_CONFIG = new MotorConfig(1, 1, 1, false, true, 10, false, false, Optional.empty());

    PIDConfig GRIPPER_PID = new PIDConfig(1,0,0,0);

}
