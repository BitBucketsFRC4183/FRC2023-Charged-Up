package org.bitbuckets.odometry;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import org.bitbuckets.lib.log.ILoggable;

public class OdometryLogAspect implements Runnable {

    final WPI_PigeonIMU pigeonIMU;
    final ILoggable<double[]> loggable;

    public OdometryLogAspect(WPI_PigeonIMU pigeonIMU, ILoggable<double[]> loggable) {
        this.pigeonIMU = pigeonIMU;
        this.loggable = loggable;
    }

    @Override
    public void run() {
        loggable.log(new double[] {
                pigeonIMU.getYaw(),
                pigeonIMU.getPitch(),
                pigeonIMU.getRoll(),
                pigeonIMU.getLastError().value
        });

    }
}
