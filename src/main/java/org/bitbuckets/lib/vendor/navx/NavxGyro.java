package org.bitbuckets.lib.vendor.navx;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.lib.hardware.IGyro;

public class NavxGyro implements IGyro {

    final AHRS ahrs;

    public NavxGyro(AHRS ahrs) {
        this.ahrs = ahrs;
    }

    @Override
    public Rotation2d getRotation2d_initializationRelative() {
        return ahrs.getRotation2d();
    }

    @Override
    public Rotation2d getRotation2d_initializationAllianceRelative() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getAllianceRelativeYaw_deg() {
        return ahrs.getYaw();
    }

    @Override
    public double getAllianceRelativePitch_deg() {
        return ahrs.getPitch();
    }

    @Override
    public double getAllianceRelativeRoll_deg() {
        return ahrs.getRoll();
    }

    @Override
    public double getAccelerationZ() {
        return ahrs.getRawAccelZ();
    }




}
