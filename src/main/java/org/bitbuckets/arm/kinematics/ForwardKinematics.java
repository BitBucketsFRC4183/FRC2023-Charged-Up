package org.bitbuckets.arm.kinematics;


import org.bitbuckets.arm.ArmConstants;


public class ForwardKinematics {
    final double theta1_degrees;
    final double theta2_degrees;

    /**
     * Parameters must be in radians
     * This class calculates the position of the end of the arm given two angles.
     * The position assumes a regular x-y coordinate system, with the origin at the base
     * of the arm.
     *
     * @param theta1_degrees angle of first joint
     * @param theta2_degrees angle of second joint
     */
    public ForwardKinematics(double theta1_degrees, double theta2_degrees) {
        this.theta1_degrees = theta1_degrees;
        this.theta2_degrees = theta2_degrees;
    }


    /**
     * This function returns the x-coordinate of the arm's final position given the 2 angles.
     * @return
     */
    public double getX() {

        double z = Math.sqrt(Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2.0) + Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2.0) + (2.0 * ArmConstants.LOWER_JOINT_LENGTH * ArmConstants.UPPER_JOINT_LENGTH * Math.cos(theta2_degrees * Math.PI/180)));
        double beta_degrees = (180 / Math.PI) * (Math.acos((Math.pow(z, 2.0) + Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2.0) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2.0)) / (2.0 * ArmConstants.LOWER_JOINT_LENGTH * z)));
        double x = z * Math.cos((Math.PI / 180) * (theta1_degrees - beta_degrees));

        return x;
    }

    /**
     * This function returns the y-coordinate of the arm's final position given the 2 angles.
     * @return
     */
    public double getY() {

        double z = Math.sqrt(Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2.0) + Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2.0) + (2.0 * ArmConstants.LOWER_JOINT_LENGTH * ArmConstants.UPPER_JOINT_LENGTH * Math.cos(theta2_degrees * Math.PI/180)));
        double beta_degrees = (180 / Math.PI) * (Math.acos((Math.pow(z, 2.0) + Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2.0) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2.0)) / (2.0 * ArmConstants.LOWER_JOINT_LENGTH * z)));
        double y = z * Math.sin((Math.PI / 180) * (theta1_degrees - beta_degrees));

        return y;

    }
}

