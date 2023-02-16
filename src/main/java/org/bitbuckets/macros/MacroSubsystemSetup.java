package org.bitbuckets.macros;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmConstants;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class MacroSubsystemSetup implements ISetup<MacroSubsystem> {

    final boolean isEnabled;

    public MacroSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public MacroSubsystem build(ProcessPath self) {
        if (!isEnabled) {
            return MockingUtil.buddy(MacroSubsystem.class);
        }

        MacroInput macroInput = new MacroInput(new Joystick(1), self.generateDebugger());

        ArmControlSetup armControlSetup = new ArmControlSetup(
                new SparkSetup(11, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID),
                new SparkSetup(9, ArmConstants.LOWER_CONFIG1, ArmConstants.LOWER_PID),
                new SparkSetup(10, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID)
        );

        DriveControlSetup driveControlSetup = new DriveControlSetup();

        ArmControl armControl = armControlSetup.build(self.addChild("arm-control"));

        Debuggable debuggable = self.generateDebugger();

        return new MacroSubsystem(macroInput, armControl, driveControl, debuggable);


    }
}
