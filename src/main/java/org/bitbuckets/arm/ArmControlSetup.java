package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.vendor.spark.SparkSetup;


public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint;
    final ISetup<IMotorController> lowerJoint1;
    final ISetup<IMotorController> upperJoint;


    public ArmControlSetup(ISetup<IMotorController> lowerJoint, ISetup<IMotorController> lowerJoint1, SparkSetup sparkSetup) {
        this.lowerJoint = lowerJoint;
        this.lowerJoint1 = lowerJoint1;
        this.upperJoint = sparkSetup;

    }


    @Override
    public ArmControl build(IProcess self) {

        var lower = self.childSetup("lower-joint", lowerJoint);
        var upper = self.childSetup("upper-joint", upperJoint);
        var lower1 = self.childSetup("lower-joint-1", lowerJoint1);
        var lowerSpark1 = lower1.rawAccess(CANSparkMax.class);
        var lowerSpark = lower.rawAccess(CANSparkMax.class);
        var upperSpark = upper.rawAccess(CANSparkMax.class);

        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
        lowerSpark1.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        lowerSpark1.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);

        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);
        lowerSpark1.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
        lowerSpark1.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 25.0);
        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -90.0);

        lowerSpark1.follow(lowerSpark);

        IDebuggable debug = self.getDebuggable();

        return new ArmControl(
                lower,
                lower1, upper,
                debug

        );
    }

    private static MechanismRoot2d getUpperPivot(Mechanism2d mech) {
        return mech.getRoot("upperPivot", 1.5, 1);
    }
}

