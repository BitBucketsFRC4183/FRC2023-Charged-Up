package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

public class ArmControlSetup implements ISetup<ArmControl> {


    @Override
    public ArmControl build(ProcessPath path) {

       var lowerJoint = new CANSparkMax(0, CANSparkMaxLowLevel.MotorType.kBrushless);
       var upperJoint = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);

        return new ArmControl(lowerJoint, upperJoint);
    }
}
