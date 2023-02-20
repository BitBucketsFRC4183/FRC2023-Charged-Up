package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.hardware.IMotorController;


public class ArmControlSetup implements ISetup<ArmControl> {

    final ISetup<IMotorController> lowerJoint1;
    final ISetup<IMotorController> lowerJoint2;
    final ISetup<IMotorController> upperJoint;
    final ISetup<IPIDCalculator> profiledLower;
    final ISetup<IPIDCalculator> profiledUpper;

    public ArmControlSetup(ISetup<IMotorController> lowerJoint1, ISetup<IMotorController> lowerJoint2, ISetup<IMotorController> upperJoint, ISetup<IPIDCalculator> profiledLower, ISetup<IPIDCalculator> profiledUpper) {
        this.lowerJoint1 = lowerJoint1;
        this.lowerJoint2 = lowerJoint2;
        this.upperJoint = upperJoint;

        this.profiledLower = profiledLower;
        this.profiledUpper = profiledUpper;
    }


    @Override
    public ArmControl build(IProcess self) {

        var lower1 = self.childSetup("lower-joint-1", lowerJoint1);
        var lower2 = self.childSetup("lower-joint-2", lowerJoint2);
        var upper = self.childSetup("upper-joint", upperJoint);

        var lowerPid = self.childSetup("lower-profiled-pid", profiledLower);
        var upperPid = self.childSetup("upper-profiled-pid", profiledUpper);

        if (self.isReal()) {
            var lowerSpark1 = lower1.rawAccess(CANSparkMax.class);
            var lowerSpark2 = lower2.rawAccess(CANSparkMax.class);
            var upperSpark = upper.rawAccess(CANSparkMax.class);
    
            lowerSpark1.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
            lowerSpark1.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);


            upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
            upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);


            lowerSpark1.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
            lowerSpark1.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

            upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 25.0);
            upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -90.0);

            lowerSpark2.follow(lowerSpark1, true);
        }


        return new ArmControl(
                lower1,
                lower2,
                upper,
                self.getDebuggable(),
                lowerPid,
                upperPid
        );
    }

}

