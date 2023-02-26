package org.bitbuckets.gripper;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;

public class GripperControlSetup implements ISetup<GripperControl> {

    final ISetup<IMotorController> gripperJoint;

    public GripperControlSetup(ISetup<IMotorController> gripperJoint) {
        this.gripperJoint = gripperJoint;
    }

    @Override
    public GripperControl build(IProcess self) {
        var gripper = self.childSetup("gripper-joint", gripperJoint);
        var gripperSpark = gripper.rawAccess(CANSparkMax.class);

        gripperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        gripperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);

        gripperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 1000);
        gripperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) 1000);

        return new GripperControl(self.getDebuggable(), gripper);
    }
}
