package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

public class ArmControl {


    final CANSparkMax lowerJoint;
    final CANSparkMax upperJoint;
    final RelativeEncoder lowerEncoder;
    final RelativeEncoder upperEncoder;


    public ArmControl(CANSparkMax lowerJoint, CANSparkMax upperJoint, RelativeEncoder lowerEncoder, RelativeEncoder upperEncoder) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = upperJoint;
        this.lowerEncoder = lowerEncoder;
        this.upperEncoder = upperEncoder;
    }

    public void calibrateLowerArm() {
        System.out.println("Calibrated lower arm!");
        lowerEncoder.setPosition(convertDegreesToRotations(-90));

    }

    public void calibrateUpperArm() {
        System.out.println("Calibrated upper arm!");
        upperEncoder.setPosition(convertDegreesToRotations(0));

    }

    // motors output in rotations converted into degrees


    public void moveLowerArm(double percentOutput) {

//        double lowerRotation = gearRatio * percentOutput / 360;
//        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
//
        //test if lower arm moves with outputs
        lowerJoint.set(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);
    }


    //sets angular position of the upper joint on the arm
    public void moveUpperArm(double percentOutput) {

//
        upperJoint.set(percentOutput * ArmConstants.CONTROL_JOINT_OUTPUT);

    }

    public void setPositionConversionFactors() {
        lowerEncoder.setPositionConversionFactor(ArmConstants.lowerArmConversionFactor);
        upperEncoder.setPositionConversionFactor(ArmConstants.upperArmConversionFactor);
    }

    public double convertDegreesToRotations(double degrees) {
        return degrees / 360.;
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