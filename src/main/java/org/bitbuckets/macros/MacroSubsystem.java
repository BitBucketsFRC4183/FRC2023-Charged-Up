package org.bitbuckets.macros;

import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.lib.log.Debuggable;

public class MacroSubsystem {

    final MacroInput macroInput;
    final Debuggable debuggable;
    final ArmControl armControl;

    MacroFSM state = MacroFSM.DISABLED;

    public MacroSubsystem(MacroInput macroInput, ArmControl armControl, Debuggable debuggable) {
        this.macroInput = macroInput;
        this.armControl = armControl;
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
