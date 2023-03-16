package org.bitbuckets.gripper;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.tune.IValueTuner;

public class GripperControlSetup implements ISetup<GripperControl> {

    final ISetup<IMotorController> gripperJoint;

    public GripperControlSetup(ISetup<IMotorController> gripperJoint) {
        this.gripperJoint = gripperJoint;
    }

    @Override
    public GripperControl build(ProcessPath self) {
        var gripper = gripperJoint.build(self.addChild("gripper"));
        var gripperSpark = gripper.rawAccess(CANSparkMax.class);

        gripperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        gripperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);

        gripperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 1000);
        gripperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) 1000);

        return new GripperControl(self.generateDebugger(), gripper);
    }
}
