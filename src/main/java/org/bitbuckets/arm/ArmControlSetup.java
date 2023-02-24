package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.math.controller.PIDController;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.hardware.IMotorController;


public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint1;
    final ISetup<IMotorController> lowerJoint2;
    final ISetup<IMotorController> upperJoint;
    final ISetup<IPIDCalculator> lowerPid;
    final ISetup<IPIDCalculator> upperPid;

    public ArmControlSetup(ISetup<IMotorController> lowerJoint1, ISetup<IMotorController> lowerJoint2, ISetup<IMotorController> upperJoint, ISetup<IPIDCalculator> lowerPid, ISetup<IPIDCalculator> upperPid) {
        this.lowerJoint1 = lowerJoint1;
        this.lowerJoint2 = lowerJoint2;
        this.upperJoint = upperJoint;
        this.lowerPid = lowerPid;
        this.upperPid = upperPid;
    }



    @Override
    public ArmControl build(IProcess self) {

        var lower1 = self.childSetup("lower-spark-1", lowerJoint1);
        var lower2 = self.childSetup("lower-spark-2", lowerJoint2);
        var upper = self.childSetup("upper-spark", upperJoint);

        IPIDCalculator lowerJointPID = self.childSetup("lower-offboard-pid", lowerPid);
        IPIDCalculator upperJointPID = self.childSetup("upper-offboard-pid", upperPid);

        lowerJointPID.rawAccess(PIDController.class).enableContinuousInput(0, Math.PI * 2.0);
        upperJointPID.rawAccess(PIDController.class).enableContinuousInput(0, Math.PI * 2.0);


        if (self.isReal()) {
            var lowerSpark1 = lower1.rawAccess(CANSparkMax.class);
            var lowerSpark2 = lower2.rawAccess(CANSparkMax.class);

            var upperSpark = upper.rawAccess(CANSparkMax.class);

            lowerSpark2.follow(lowerSpark1, true);
        }


        return new ArmControl(
                lower1,
                upper,
                self.getDebuggable(),
                lowerJointPID,
                upperJointPID);
    }

}

