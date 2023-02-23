package org.bitbuckets.arm.kinematics;

import org.bitbuckets.arm.ArmConstants;


public class InverseKinematics {

    final double x;
    final double y;

    /**
     * Parameters must be in meters
     * This class calculates the two angles of the joints given a position of the end of the arm.
     * The position assumes a regular x-y coordinate system, with the origin at the base
     * of the arm.
     *
     * @param x x-coordinate of final position
     * @param y y-coordinate of final position
     */
    public InverseKinematics(double x, double y) {
        this.x = x;
        this.y = y;
    }





    /**
     * This function returns the degree of the lower joint given a final position.
     * Note that all inverse trigonometric functions return radians
     * @return
     */
    public double getLowerJoint_degrees() {
        double c = Math.sqrt(x * x + y * y);
        double alpha = Math.atan(y / x);
        return (180. / Math.PI) * (Math.acos((Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) + Math.pow(c, 2) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2)) / (2 * ArmConstants.LOWER_JOINT_LENGTH * c)) + alpha);
    }


    /**
     * This function returns the degree of the upper joint given a final position.
     * Note that all inverse trigonometric functions return radians
     * @return
     */
    public double getUpperJoint_degrees() {
        double c = Math.sqrt(x * x + y * y);
        double beta = Math.acos((Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) + Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2) - Math.pow(c, 2)) / (2 * ArmConstants.UPPER_JOINT_LENGTH * ArmConstants.LOWER_JOINT_LENGTH));

        return -((180./Math.PI) * (Math.PI - beta));
    }
}
