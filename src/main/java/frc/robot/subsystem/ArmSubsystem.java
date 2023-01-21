package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.robot.config.Config;

public class ArmSubsystem extends BitBucketsSubsystem {
    //make motors
    private CANSparkMax lowerJoint;

    public void invertLower(boolean isInverted) {
        lowerJoint.setInverted(isInverted);
    }

    private CANSparkMax upperJoint;

    public void invertUpper(boolean isInverted) {
        upperJoint.setInverted(isInverted);
    }


    private double CONTROL_JOINT_OUTPUT = 0.1;

    //calculated gearRatio
    private double gearRatio = (5 * 4 * 3) / (12. / 30.);

    //used control return on mac or alt enter on windows for this function, init function, periodic function, and disable function
    public ArmSubsystem(Config config) {
        super(config);
    }


    @Override
    public void init() {
        //instantiated CANSparkMax motors
        lowerJoint = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        upperJoint = new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);

    }


    public void moveArmTo(double lowerJointDegrees, double upperJointDegrees) {
        // lowerJointDegrees / 360 -> rotations of lower joint, gear Ratio for weird stuff inside motor
        double lowerRotation = gearRatio * lowerJointDegrees / 360;
        double upperRotation = gearRatio * upperJointDegrees / 360;

        //set lowerJoint to lowerRotation and upperJoint to upperRotation
        //note: change position to smart motion later, also needs PID constants (1/15)
        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
        upperJoint.getPIDController().setReference(upperRotation, CANSparkMax.ControlType.kPosition);

    }

    public void moveLowerArm(double lowerJointOutput) {

        //test if lower arm moves with outputs
        lowerJoint.set(lowerJointOutput * CONTROL_JOINT_OUTPUT);
    }

    public void moveUpperArm(double upperJointOutput) {
        //test if upper arm moves with outputs
        upperJoint.set(upperJointOutput * CONTROL_JOINT_OUTPUT);
    }

    public void stopLower() {
        lowerJoint.set(0);
    }

    public void stopUpper() {
        upperJoint.set(0);
    }

    @Override
    public void periodic() {

    }

    @Override
    public void disable() {

    }


}
