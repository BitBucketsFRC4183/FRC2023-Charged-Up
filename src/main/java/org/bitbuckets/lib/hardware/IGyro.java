package org.bitbuckets.lib.hardware;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public interface IGyro {

    Rotation2d getRotation2d();

    double getYaw_deg();
    double getPitch_deg();
    double getRoll_deg();

    /**
     * Get acceleration on the Z axis. Confusing as heck and i wish i knew what the Z axis
     * @return
     */
    double getAccelerationZ();

    void zero();

    void zero();
}
