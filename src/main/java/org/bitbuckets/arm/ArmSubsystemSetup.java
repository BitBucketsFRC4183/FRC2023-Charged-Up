package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.gripper.GripperControl;
import org.bitbuckets.gripper.GripperControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final boolean isEnabled;

    public ArmSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public ArmSubsystem build(ProcessPath self) {
        if (!isEnabled) {
            return MockingUtil.buddy(ArmSubsystem.class);
        }

        ArmControlSetup armControlSetup = new ArmControlSetup(
                new SparkSetup(11, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID),
                new SparkSetup(9, ArmConstants.LOWER_CONFIG1, ArmConstants.LOWER_PID),
                new SparkSetup(10, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID)
        );

        ArmControl armControl = armControlSetup.build(self.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1), self.generateDebugger());
        GripperControl gripperControl = GripperControlSetup.build(self.addChild("gripper-control"));
        Debuggable debuggable = self.generateDebugger();

        return new ArmSubsystem(armInput, armControl, gripperControl, debuggable);

    }
}
