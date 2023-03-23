package org.bitbuckets.lib.vendor.noops;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

/**
 * Does nothing
 */
public class NoopsAbsoluteEncoder implements IAbsoluteEncoder {

    public final static ISetup<IAbsoluteEncoder> SETUP = a -> new NoopsAbsoluteEncoder();

    @Override
    public double getAbsoluteAngle() {
        return 0;
    }

}
