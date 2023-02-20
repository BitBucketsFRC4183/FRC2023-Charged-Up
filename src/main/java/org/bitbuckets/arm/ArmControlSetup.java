package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDConfig;
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

        ProfiledPIDFSetup lowerJointSetupPID = new ProfiledPIDFSetup(new PIDConfig(0, 0, 0, 0), new TrapezoidProfile.Constraints(0, 0));
        IPIDCalculator lowerJointPID = lowerJointSetupPID.build(self.addChild("lowerJointPID"));
        ProfiledPIDFSetup upperJointSetupPID = new ProfiledPIDFSetup(new PIDConfig(0, 0, 0, 0), new TrapezoidProfile.Constraints(0, 0));
        IPIDCalculator upperJointPID = upperJointSetupPID.build(self.addChild("upperJointPID"));


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

