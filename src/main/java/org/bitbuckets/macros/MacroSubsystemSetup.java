package org.bitbuckets.macros;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.util.MockingUtil;

public class MacroSubsystemSetup implements ISetup<MacroSubsystem> {

    final boolean isEnabled;

    public MacroSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public MacroSubsystem build(IProcess self) {
        if (!isEnabled) {
            return MockingUtil.buddy(MacroSubsystem.class);
        }

        MacroControl macroControl = self.childSetup("macro-control", new MacroControlSetup());
        MacroInput macroInput = new MacroInput(new Joystick(1), self.getDebuggable());

        return new MacroSubsystem(macroInput, macroControl, self.getDebuggable());


    }
}
