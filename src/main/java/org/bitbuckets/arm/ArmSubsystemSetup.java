package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final static boolean armEnabled = false;

    static ArmControl buildArmControl(ProcessPath path) {
        if (!armEnabled) {
            return MockingUtil.buddy(ArmControl.class);
        }

        //labels: high priority
        ArmControlSetup armControlSetup = new ArmControlSetup(
                new SparkSetup(11, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID),
                new SparkSetup(12, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID)
        );

        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));
        return armControl;
    }
    @Override
    public ArmSubsystem build(ProcessPath path) {
        ArmControl armControl = buildArmControl(path);
        ArmInput armInput = new ArmInput(new Joystick(1));

        return new ArmSubsystem(armInput, armControl, path.generateStringLogger("arm-subsystem"));

    }
}
