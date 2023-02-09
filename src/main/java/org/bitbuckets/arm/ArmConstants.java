package org.bitbuckets.arm;

import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;

import java.util.Optional;

public interface ArmConstants {

    // calculated gearRatio
    // Input to output- 5:1 4:1 3:1
    // Final gear different for each arm; 12:26 for lower and 12:30 for upper
    double LOWER_ARM_GEAR_RATIO = 1.0 / (5.0 * 4.0 * 3.0) * (12. / 26.); // encoder rotations to mechanism rotations; 130 encoder rotations = 1 mechanism rotation
    double UPPER_ARM_GEAR_RATIO = 1.0 / (5.0 * 4.0 * 3.0) * (12. / 30.); // encoder rotations to mechanism rotations; 130 encoder rotations = 1 mechanism rotation

    MotorConfig LOWER_CONFIG = new MotorConfig(ArmConstants.LOWER_ARM_GEAR_RATIO, 1, 1, false, true, 20.0, false, false, Optional.empty());
    MotorConfig UPPER_CONFIG = new MotorConfig(ArmConstants.UPPER_ARM_GEAR_RATIO, 1, 1, true, true, 20.0, false, false, Optional.empty());

    PIDConfig LOWER_PID = new PIDConfig(0, 0, 0, 0);
    PIDConfig UPPER_PID = new PIDConfig(0, 0, 0, 0);
    //change these
    double LOWER_JOINT_LENGTH = 0.695;
    double UPPER_JOINT_LENGTH = 0.84;


    // Feel free to change to make arms faster/slower for input
    double CONTROL_JOINT_OUTPUT = 0.15;

    /*
    Starting Configuration
    0 = Horizontal
    lowerJoint = 90
    upperJoint = 0
     */

    // Neo Brushless Motor Relative Encoder: 42 counts per revolution

    double STORAGE_X = 0.3;
    double STORAGE_Y = 0.3;

    double PREPARE_X = -0.2;
    double PREPARE_Y = 0.6;

    double HUMAN_INTAKE_X = 0.5;
    double HUMAN_INTAKE_Y = 0.5;

    double MID_NODE_X = 0.8;
    double MID_NODE_Y = 0.8;

    double HIGH_NODE_X = 0.8;
    double HIGH_NODE_Y = 0.8;


}
