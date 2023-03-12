package org.bitbuckets.vision;

import edu.wpi.first.math.geometry.Transform3d;

public class VisionConfig {

    public final double cameraFOV_degrees;
    public final Transform3d robotToCamera;
    public final double maxLEDRange;
    public final int cameraResWidth;
    public final int cameraResHeight;
    public final double minTargetArea;
    public final double cameraHeight_meters;
    public final double tagHeight_meters;
    public final double cameraPitch_radians;


    public VisionConfig(double cameraFOV_degrees, Transform3d robotToCamera, double maxLEDRange, int cameraResWidth, int cameraResHeight, double minTargetArea, double cameraHeight_meters, double tagHeight_meters, double cameraPitch_radians) {
        this.cameraFOV_degrees = cameraFOV_degrees;
        this.robotToCamera = robotToCamera;
        this.maxLEDRange = maxLEDRange;
        this.cameraResWidth = cameraResWidth;
        this.cameraResHeight = cameraResHeight;
        this.minTargetArea = minTargetArea;
        this.cameraHeight_meters = cameraHeight_meters;
        this.tagHeight_meters = tagHeight_meters;
        this.cameraPitch_radians = cameraPitch_radians;
    }
}
