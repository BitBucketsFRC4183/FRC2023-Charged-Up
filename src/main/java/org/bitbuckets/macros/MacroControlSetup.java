package org.bitbuckets.macros;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.Debuggable;

public class MacroControlSetup implements ISetup<MacroControl> {
    @Override
    public MacroControl build(ProcessPath self) {

        Debuggable debug = self.generateDebugger();

        return new MacroControl(debug);
    }
}
