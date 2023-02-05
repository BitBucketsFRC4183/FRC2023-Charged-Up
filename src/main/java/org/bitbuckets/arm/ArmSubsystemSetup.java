package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final boolean isEnabled;

    public ArmSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public ArmSubsystem build(ProcessPath path) {
        if (!isEnabled) {
            return MockingUtil.buddy(ArmSubsystem.class);
        }

        ArmControlSetup armControlSetup = new ArmControlSetup(
                new SparkSetup(10, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID),
                new SparkSetup(9, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID)
        );

        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1));

        return new ArmSubsystem(armInput, armControl, path.generateStringLogger("arm-log"));

    }
}
