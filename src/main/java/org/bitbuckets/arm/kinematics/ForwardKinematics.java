package org.bitbuckets.arm.kinematics;


import config.Arm;


public class ForwardKinematics {
    final double theta1;
    final double theta2;

    /**
     * Parameters must be in radians
     * This class calculates the position of the end of the arm given two angles.
     * The position assumes a regular x-y coordinate system, with the origin at the base
     * of the arm.
     *
     * @param theta1 angle of first joint
     * @param theta2 angle of second joint
     */
    public ForwardKinematics(double theta1, double theta2) {
        this.theta1 = theta1;
        this.theta2 = theta2;
    }


    /**
     * This function returns the x-coordinate of the arm's final position given the 2 angles.
     * @return
     */
    public double getX() {

        double z = Math.sqrt(Math.pow(Arm.LOWER_JOINT_LENGTH, 2.0) + Math.pow(Arm.UPPER_JOINT_LENGTH, 2.0) + (2.0 * Arm.LOWER_JOINT_LENGTH * Arm.UPPER_JOINT_LENGTH * Math.cos(theta2)));
        double beta = Math.acos((Math.pow(z, 2.0) + Math.pow(Arm.LOWER_JOINT_LENGTH, 2.0) - Math.pow(Arm.UPPER_JOINT_LENGTH, 2.0)) / (2.0 * Arm.LOWER_JOINT_LENGTH * z));
        double x = z * Math.cos(theta1 - beta);

        return x;
    }

    /**
     * This function returns the y-coordinate of the arm's final position given the 2 angles.
     * @return
     */
    public double getY() {

        double z = Math.sqrt(Math.pow(Arm.LOWER_JOINT_LENGTH, 2.0) + Math.pow(Arm.UPPER_JOINT_LENGTH, 2.0) + (2.0 * Arm.LOWER_JOINT_LENGTH * Arm.UPPER_JOINT_LENGTH * Math.cos(theta2)));
        double beta = Math.acos((Math.pow(z, 2.0) + Math.pow(Arm.LOWER_JOINT_LENGTH, 2.0) - Math.pow(Arm.UPPER_JOINT_LENGTH, 2.0)) / (2.0 * Arm.LOWER_JOINT_LENGTH * z));
        double y = z * Math.sin(theta1 - beta);

        return y;
    }
}

