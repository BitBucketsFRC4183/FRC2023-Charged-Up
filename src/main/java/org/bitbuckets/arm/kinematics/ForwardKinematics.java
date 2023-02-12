package org.bitbuckets.arm.kinematics;


import org.bitbuckets.arm.ArmConstants;

public class ForwardKinematics {
    final double theta1;
    final double theta2;

    // Parameters must be in radians
    public ForwardKinematics(double theta1, double theta2) {
        this.theta1 = theta1;
        this.theta2 = theta2;

    }


    public double getX() {

        double z = Math.sqrt(Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) + Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2) + (2 * ArmConstants.LOWER_JOINT_LENGTH * ArmConstants.UPPER_JOINT_LENGTH * Math.cos(theta2)));
        double beta = Math.acos((Math.pow(z, 2) + Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2)) / (2 * ArmConstants.LOWER_JOINT_LENGTH * z));
        /*
        System.out.println(z);
        System.out.println(beta);
        System.out.println((Math.pow(z, 2) + Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2)));
        System.out.println((2 * ArmConstants.LOWER_JOINT_LENGTH * z));
        */
        double x = z * Math.cos(theta1 - beta);

        return z;
    }


    public double getY() {

        double z = Math.sqrt(Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) + Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2) + (2 * ArmConstants.LOWER_JOINT_LENGTH * ArmConstants.UPPER_JOINT_LENGTH * Math.cos(theta2)));
        double beta = Math.acos((Math.pow(z, 2) + Math.pow(ArmConstants.LOWER_JOINT_LENGTH, 2) - Math.pow(ArmConstants.UPPER_JOINT_LENGTH, 2)) / (2 * ArmConstants.LOWER_JOINT_LENGTH * z));
        double y = z * Math.sin(theta1 - beta);

        return y;
    }
}

