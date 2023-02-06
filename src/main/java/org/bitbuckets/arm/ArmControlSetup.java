package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint;
    final ISetup<IMotorController> upperJoint;


    public ArmControlSetup(ISetup<IMotorController> lowerJoint, SparkSetup sparkSetup) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = sparkSetup;

    }


    @Override
    public ArmControl build(ProcessPath path) {

        var lower = lowerJoint.build(path.addChild("lower-joint"));
        var upper = upperJoint.build(path.addChild("upper-joint"));

        var lowerSpark = lower.rawAccess(CANSparkMax.class);
        var upperSpark = upper.rawAccess(CANSparkMax.class);

        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);

        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 25.0);
        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -90.0);



        return new ArmControl(
                lower,
                upper,
                path.generateBooleanLogger("isArmOutOfReach"),
                path.generateDoubleLogger("convertUpperRawToMechanism"),
                path.generateDoubleLogger("convertLowerRawToMechanism"),
                path.generateDoubleLogger("findLowerAngleKinematics"),
                path.generateDoubleLogger("findUpperAngleKinematics"));
    }
}

