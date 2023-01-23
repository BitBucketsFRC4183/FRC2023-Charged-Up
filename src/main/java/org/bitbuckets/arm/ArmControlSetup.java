package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

public class ArmControlSetup implements ISetup<ArmControl> {


    @Override
    public ArmControl build(ProcessPath path) {

        var lowerJoint = new CANSparkMax(9, CANSparkMaxLowLevel.MotorType.kBrushless);
        var upperJoint = new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);

        lowerJoint.getPIDController().setP(ArmConstants.kP);
        lowerJoint.getPIDController().setI(ArmConstants.kI);
        lowerJoint.getPIDController().setD(ArmConstants.kD);

        upperJoint.getPIDController().setP(ArmConstants.kP);
        upperJoint.getPIDController().setI(ArmConstants.kI);
        upperJoint.getPIDController().setD(ArmConstants.kD);


        return new ArmControl(lowerJoint, upperJoint);
    }
}
