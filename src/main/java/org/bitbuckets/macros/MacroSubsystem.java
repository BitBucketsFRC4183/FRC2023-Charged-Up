package org.bitbuckets.macros;

import org.bitbuckets.lib.log.IDebuggable;

public class MacroSubsystem {

    final MacroInput macroInput;
    final MacroControl macroControl;
    final IDebuggable debuggable;

    MacroFSM state = MacroFSM.DISABLED;

    public MacroSubsystem(MacroInput macroInput, MacroControl macroControl, IDebuggable debuggable) {
        this.macroInput = macroInput;
        this.macroControl = macroControl;
        this.debuggable = debuggable;
    }

    public void teleopPeriodic() {

        switch (state) {

            case DISABLED:
                //default
            case MACRO1:
                //insert macro1 here
            case MACRO2:
                //insert macro2 here
        }

        debuggable.log("state", state);

    }

}
