package org.bitbuckets.macros;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IDebuggable;


public class MacroControlSetup implements ISetup<MacroControl> {
    @Override
    public MacroControl build(IProcess self) {

        IDebuggable debug = self.getDebuggable();

        return new MacroControl(debug);
    }
}
