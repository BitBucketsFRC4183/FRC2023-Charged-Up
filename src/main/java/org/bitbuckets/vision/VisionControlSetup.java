package org.bitbuckets.vision;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.photonvision.PhotonCamera;

public class VisionControlSetup implements ISetup<VisionControl> {
    @Override
    public VisionControl build(ProcessPath path) {
        return new VisionControl();
    }

    static PhotonCamera camera = new PhotonCamera("Arducam_OV9281_USB_Camera");
}
