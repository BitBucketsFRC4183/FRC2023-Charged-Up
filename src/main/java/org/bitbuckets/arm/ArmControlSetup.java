package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.ProfiledPIDFCalculator;
import org.bitbuckets.lib.control.ProfiledPIDFSetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;


public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint1;
    final ISetup<IMotorController> lowerJoint2;
    final ISetup<IMotorController> upperJoint;

    public ArmControlSetup(ISetup<IMotorController> lowerJoint1, ISetup<IMotorController> lowerJoint2, ISetup<IMotorController> upperJoint) {
        this.lowerJoint1 = lowerJoint1;
        this.lowerJoint2 = lowerJoint2;
        this.upperJoint = upperJoint;

    }


    @Override
    public ArmControl build(ProcessPath self) {

        var lower1 = lowerJoint1.build(self.addChild("lower-joint-1"));
        var lower2 = lowerJoint2.build(self.addChild("lower-joint-2"));
        var upper = upperJoint.build(self.addChild("upper-joint"));

        ProfiledPIDFSetup lowerJointSetupPID = new ProfiledPIDFSetup();
        IPIDCalculator lowerJointPID = lowerJointSetupPID.build(self.addChild("lowerJointPID"));
        ProfiledPIDFSetup upperJointSetupPID = new ProfiledPIDFSetup();
        IPIDCalculator upperJointPID = upperJointSetupPID.build(self.addChild("upperJointPID"));


        if (self.isReal())
        {
            var lowerSpark1 = lower1.rawAccess(CANSparkMax.class);
            var lowerSpark2 = lower2.rawAccess(CANSparkMax.class);
            var upperSpark = upper.rawAccess(CANSparkMax.class);

            lowerSpark1.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, false);
            lowerSpark1.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, false);

            lowerSpark2.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, false);
            lowerSpark2.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, false);

            upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, false);
            upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, false);


            lowerSpark1.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
            lowerSpark1.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

            lowerSpark2.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
            lowerSpark2.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

            upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 25.0);
            upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -90.0);

            lowerSpark1.follow(lowerSpark2);
        }

        Debuggable debug = self.generateDebugger();

        return new ArmControl(
                lower1,
                lower2,
                upper,
                debug,
                lowerJointPID,
                upperJointPID);
    }

}

