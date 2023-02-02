package org.bitbuckets.arm;

import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;

import java.util.Optional;

public interface ArmConstants {

    // calculated gearRatio
    // Input to output- 5:1 4:1 3:1
    // Final gear different for each arm; 12:26 for lower and 12:30 for upper
    double LOWER_ARM_GEAR_RATIO = (5 * 4 * 3) * (12. / 26.); //roughly 27.69
    double UPPER_ARM_GEAR_RATIO = (5 * 4 * 3) * (12. / 30.);

    MotorConfig LOWER_CONFIG = new MotorConfig(ArmConstants.LOWER_ARM_GEAR_RATIO, 1, 1, false, true, 20.0, false, false, Optional.empty());
    MotorConfig UPPER_CONFIG = new MotorConfig(ArmConstants.UPPER_ARM_GEAR_RATIO, 1, 1, false, true, 20.0, false, false, Optional.empty());

    PIDConfig LOWER_PID = new PIDConfig(0,0,0,0);
    PIDConfig UPPER_PID = new PIDConfig(0,0,0,0);
    //change these
    double lowerJointLength = 2.;
    double upperJointLength = 2.;

    // Feel free to change to make arms faster/slower for input
    double CONTROL_JOINT_OUTPUT = 0.3;

    /*
    Starting Configuration
    0 = Horizontal
    lowerJoint = 90
    upperJoint = 0
     */

    // Neo Brushless Motor Relative Encoder: 42 counts per revolution


}
