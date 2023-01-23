package org.bitbuckets.arm;

import edu.wpi.first.math.controller.PIDController;

public interface ArmConstants {

    // calculated gearRatio
    // Input to output- 5:1 4:1 3:1
    // Final gear different for each arm; 12:26 for lower and 12:30 for upper
    final double lowerArmGearRatio = (5 * 4 * 3) * (12. / 26.);
    final double upperArmGearRation = (5 * 4 * 3) * (12. / 30.);

    final double lowerArmConversionFactor = (5 * 4 * 3) * (12. / 26.) * 360;
    final double upperArmConversionFactor = (5 * 4 * 3) * (12. / 30.) * 360;


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
    final double kP = 1;
    final double kI = 0;
    final double kD = 0;
    PIDController armPID = new PIDController(kP, kI, kD);


}
