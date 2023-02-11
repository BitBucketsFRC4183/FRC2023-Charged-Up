package org.bitbuckets.arm.kinematics;

import org.bitbuckets.arm.ArmConstants;
import org.opencv.core.Mat;

public class InverseKinematics {

    final double x;
    final double y;

    public InverseKinematics(double x, double y) {
        this.x = x;
        this.y = y;
    }


    //joint 1
    // Result is in radians
    public double getLowerJoint_degrees() {
        double c = Math.sqrt(x * x + y * y);
        double alpha = Math.atan(y / x);
        // Math.pow takes first double variable to the 2nd double variable power
        // lowerJointLength^2 + c^2 +
        //double lowerJointAngle = (Math.pow(ArmConstants.lowerJointLength, 2) + Math.pow(c, 2) + Math.pow(ArmConstants.upperJointLength, 2)) / (2 * ArmConstants.lowerJointLength * c) - alpha;

        return (180. / Math.PI) * (Math.acos((Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) + Math.pow(c, 2) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2)) / (2 * ArmConstants.LOWER_JOINT_LENGTH * c)) - alpha);
    }

    // Result is in radians
    public double getUpperJoint_degrees() {
        double c = Math.sqrt(x * x + y * y);
        double beta = Math.acos((Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) + Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2) - Math.pow(c, 2)) / (2 * ArmConstants.UPPER_JOINT_LENGTH * ArmConstants.LOWER_JOINT_LENGTH));

        return (180./Math.PI) * (Math.PI - beta);
    }
}
