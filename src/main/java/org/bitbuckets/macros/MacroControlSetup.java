package org.bitbuckets.macros;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;

public class MacroControlSetup implements ISetup<MacroControl> {
    @Override
    public MacroControl build(IProcess self) {

        return new MacroControl(self.getDebuggable());
    }
}
