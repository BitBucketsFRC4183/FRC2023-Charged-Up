package org.bitbuckets.lib.vendor.thrifty;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.StartupLogger;

public class ThriftyEncoderSetup implements ISetup<IAbsoluteEncoder> {

    final int canId;

    public ThriftyEncoderSetup(int canId) {
        this.canId = canId;
    }

    @Override
    public IAbsoluteEncoder build(ProcessPath path) {

        StartupLogger libSetup = path.generateStartupLogger("lib-setup");


        return null;
    }

}
