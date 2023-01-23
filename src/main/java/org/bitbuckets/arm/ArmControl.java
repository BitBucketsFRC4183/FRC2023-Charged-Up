package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;

public class ArmControl {


    final CANSparkMax lowerJoint;
    final CANSparkMax upperJoint;

    private double CONTROL_JOINT_OUTPUT = 0.3;

    //calculated gearRatio
    private double gearRatio = (5 * 4 * 3) / (12. / 30.);

    public ArmControl(CANSparkMax lowerJoint, CANSparkMax upperJoint) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;
    }

    //sets angular position of the lower joint on the arm
    public void moveLowerArm(double percentOutput) {

//        double lowerRotation = gearRatio * percentOutput / 360;
//        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
//
        //test if lower arm moves with outputs
        lowerJoint.set(percentOutput * CONTROL_JOINT_OUTPUT);
    }


    //sets angular position of the upper joint on the arm
    public void moveUpperArm(double percentOutput) {

//
        upperJoint.set(percentOutput * CONTROL_JOINT_OUTPUT);

    }


    public void moveToLowPos() {

    }


    public void movetoHighPos() {

    }

    public void moveToMidPos() {

    }


    public void stopLower() {
        lowerJoint.set(0);
    }

    public void stopUpper() {
        upperJoint.set(0);
    }

}