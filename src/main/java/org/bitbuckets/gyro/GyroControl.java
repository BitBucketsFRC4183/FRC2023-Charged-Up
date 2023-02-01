package org.bitbuckets.gyro;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.geometry.Rotation2d;
import org.bitbuckets.lib.log.ILoggable;

public class GyroControl implements Runnable{

    final WPI_PigeonIMU pigeonIMU;

    final ILoggable<double[]> loggable;

    GyroControl(WPI_PigeonIMU pigeonIMU, ILoggable<double[]> loggable) {
        this.pigeonIMU = pigeonIMU;
        this.loggable = loggable;
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

    @Override
    public void run() {
        loggable.log(new double[] {pigeonIMU.getAngle()});

    }
}
