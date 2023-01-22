package org.bitbuckets.vision;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

public class VisionControlSetup implements ISetup<VisionControl> {
    @Override
    public VisionControl build(ProcessPath path) {
        return new VisionControl();
    }
}
