package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

public class ArmControlSetup implements ISetup<ArmControl> {

    //TODO make this use IMotorControllers

    final ISetup<IMotorController> lowerJointSetup;
    final ISetup<IMotorController> upperJointSetup;

    public ArmControlSetup(ISetup<IMotorController> lowerJointSetup, ISetup<IMotorController> upperJointSetup) {
        this.lowerJointSetup = lowerJointSetup;
        this.upperJointSetup = upperJointSetup;
    }

    @Override
    public ArmControl build(ProcessPath path) {
        // use setups, not canSparkMax ctor

        CANSparkMax lowerJoint = new CANSparkMax(9, CANSparkMaxLowLevel.MotorType.kBrushless);
        CANSparkMax upperJoint = new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);

        lowerJoint.getPIDController().setP(ArmConstants.kP);
        lowerJoint.getPIDController().setI(ArmConstants.kI);
        lowerJoint.getPIDController().setD(ArmConstants.kD);

        upperJoint.getPIDController().setP(ArmConstants.kP);
        upperJoint.getPIDController().setI(ArmConstants.kI);
        upperJoint.getPIDController().setD(ArmConstants.kD);


        return new ArmControl(lowerJoint, upperJoint, lowerJoint.getEncoder(), upperJoint.getEncoder());
    }
}
