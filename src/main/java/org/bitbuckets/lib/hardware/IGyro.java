package org.bitbuckets.lib.hardware;

import edu.wpi.first.math.geometry.Rotation2d;

public interface IGyro {

    Rotation2d getRotation2d_initializationRelative();
    Rotation2d getRotation2d_initializationAllianceRelative();

    //void forceToRotation(Rotation2d rotation2d);

    double getAllianceRelativeYaw_deg();
    double getAllianceRelativePitch_deg();
    double getAllianceRelativeRoll_deg();



    /**
     * Get acceleration on the Z axis. Confusing as heck and i wish i knew what the Z axis
     * @return
     */
    double getAccelerationZ();


}
