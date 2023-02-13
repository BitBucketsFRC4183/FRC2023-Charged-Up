package org.bitbuckets.macros;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmInput;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;

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

        MacroControlSetup macroControlSetup = new MacroControlSetup();

        MacroControl macroControl = macroControlSetup.build(self.addChild("arm-control"));
        MacroInput macroInput = new MacroInput(new Joystick(1), self.generateDebugger());
        Debuggable debuggable = self.generateDebugger();

        return new MacroSubsystem(macroInput, macroControl, debuggable);


    }
}
