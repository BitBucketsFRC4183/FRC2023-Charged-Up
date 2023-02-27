package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.gripper.GripperControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.util.MockingUtil;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final AutoSubsystem autoSubsystem;
    final OperatorInput operatorInput;

    final ISetup<ArmControl> armControlISetup;
    final ISetup<GripperControl> gripperControlISetup;

    final boolean isEnabled;

    public ArmSubsystemSetup(AutoSubsystem autoSubsystem, OperatorInput operatorInput, ISetup<ArmControl> armControlISetup, ISetup<GripperControl> gripperControlISetup, boolean isEnabled) {
        this.autoSubsystem = autoSubsystem;
        this.operatorInput = operatorInput;
        this.armControlISetup = armControlISetup;
        this.gripperControlISetup = gripperControlISetup;
        this.isEnabled = isEnabled;
    }

    @Override
    public ArmSubsystem build(IProcess self) {
        if (!isEnabled) {
            return MockingUtil.buddy(ArmSubsystem.class);
        }
        GripperInput gripperInput = new GripperInput(new Joystick(1));

        return new ArmSubsystem(operatorInput, armControlISetup, gripperControlISetup, gripperInput, self.getDebuggable(), autoSubsystem);

    }
}
