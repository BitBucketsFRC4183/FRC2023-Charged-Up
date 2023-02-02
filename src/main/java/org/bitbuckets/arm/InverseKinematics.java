package org.bitbuckets.arm;

public class InverseKinematics {

    final double x;
    final double y;

    public InverseKinematics(double x, double y) {
        this.x = x;
        this.y = y;
    }


    //joint 1
    // Result is in radians
    public double getLowerJointAngle() {
        double c = Math.sqrt(x * x + y * y);
        double alpha = Math.atan(y / x);
        // Math.pow takes first double variable to the 2nd double variable power
        // lowerJointLength^2 + c^2 +
        double lowerJointAngle = Math.acos((Math.pow(ArmConstants.lowerJointLength, 2) + Math.pow(c, 2) - Math.pow(ArmConstants.upperJointLength, 2)) / (2 * ArmConstants.lowerJointLength * c)) - alpha;
        //double lowerJointAngle = (Math.pow(ArmConstants.lowerJointLength, 2) + Math.pow(c, 2) + Math.pow(ArmConstants.upperJointLength, 2)) / (2 * ArmConstants.lowerJointLength * c) - alpha;

        return lowerJointAngle;
    }

    // Result is in radians
    public double getUpperJointAngle() {
        double c = Math.sqrt(x * x + y * y);
        double beta = Math.acos((Math.pow(ArmConstants.lowerJointLength, 2) + Math.pow(ArmConstants.upperJointLength, 2) - Math.pow(c, 2)) / (2 * ArmConstants.upperJointLength * ArmConstants.lowerJointLength));
        double upperJointAngle = Math.PI - beta;

        return upperJointAngle;
    }
}