package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.robot.config.Config;

public class ArmSubsystem extends BitBucketsSubsystem{
    //make motors
    private CANSparkMax lowerJoint;
    private CANSparkMax upperJoint;

    //calculated gearRatio
    private double gearRatio = (5 * 4 * 3)/(12/30);

    //used control return on mac or alt enter on windows for this function, init function, periodic function, and disable function
    protected ArmSubsystem(Config config) {
        super(config);
    }


    @Override
    public void init() {
        //instantiated CANSparkMax motors
        lowerJoint = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);
        upperJoint = new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);

    }


    public void moveArm(double lowerJointDegrees, double upperJointDegrees){
        // lowerJointDegrees / 360 -> rotations of lower joint, gear Ratio for weird stuff inside motor
        double lowerRotation = gearRatio * lowerJointDegrees / 360;
        double upperRotation = gearRatio * upperJointDegrees / 360;

        //set lowerJoint to lowerRotation and upperJoint to upperRotation
        //note: change position to smart motion later, also needs PID constants (1/15)
        lowerJoint.getPIDController().setReference(lowerRotation, CANSparkMax.ControlType.kPosition);
        upperJoint.getPIDController().setReference(upperRotation, CANSparkMax.ControlType.kPosition);

    }

    @Override
    public void periodic() {

    }

    @Override
    public void disable() {

    }



}
