package org.bitbuckets.arm;

public interface ArmConstants {

    // calculated gearRatio
    // Input to output- 5:1 4:1 3:1
    // Final gear different for each arm; 12:26 for lower and 12:30 for upper
    double lowerArmGearRatio = (5 * 4 * 3) * (12. / 26.); //roughly 27.69
    double upperArmGearRatio = (5 * 4 * 3) * (12. / 30.);

    double lowerArmConversionFactor = (5 * 4 * 3) * (12. / 26.) * 360;
    double upperArmConversionFactor = (5 * 4 * 3) * (12. / 30.) * 360;


    // Feel free to change to make arms faster/slower for input
    final double CONTROL_JOINT_OUTPUT = 0.3;

    /*
    Starting Configuration
    0 = Horizontal
    lowerJoint = 90
    upperJoint = 0
     */

    // Neo Brushless Motor Relative Encoder: 42 counts per revolution


    // PID constants
    double kP = 1;
    double kI = 0;
    double kD = 0;


}
