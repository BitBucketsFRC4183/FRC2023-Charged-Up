package org.bitbuckets.gyro;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.ILoggable;

public class GyroControlSetup implements ISetup<GyroControl> {

    final int canId;

    public GyroControlSetup(int canId) {
        this.canId = canId;
    }

    @Override
    public GyroControl build(ProcessPath path) {
        WPI_PigeonIMU pigeonIMU = new WPI_PigeonIMU(canId);

        pigeonIMU.calibrate(); //calibrate on spawn

        ILoggable<double[]> loggable = path.generateDoubleLoggers("gyro-angle");
        return new GyroControl(pigeonIMU, loggable);
    }
}
