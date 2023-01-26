package org.bitbuckets.gyro;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.geometry.Rotation2d;

public class GyroControl {

    final WPI_PigeonIMU pigeonIMU;

    GyroControl(WPI_PigeonIMU pigeonIMU) {
        this.pigeonIMU = pigeonIMU;
    }


    public double[] getGyroXYZ_dps() {
        double[] velArray1 = new double[]{0, 0, 0};
        pigeonIMU.getRawGyro(velArray1);
        return velArray1;
    }


    public double getRoll_deg() {
        return pigeonIMU.getRoll();

    }

    public void zeroGyro() {
        pigeonIMU.reset();
    }


    public Rotation2d getGyroAngle() {
        return pigeonIMU.getRotation2d();
    }

    public double getYaw_deg() {
        return pigeonIMU.getYaw();
    }
}
