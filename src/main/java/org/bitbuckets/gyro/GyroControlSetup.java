package org.bitbuckets.gyro;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

public class GyroControlSetup implements ISetup<GyroControl> {

    final int canId;

    public GyroControlSetup(int canId) {
        this.canId = canId;
    }

    @Override
    public GyroControl build(ProcessPath path) {
        DataLogger<GyroDataAutoGen> logger = path.generatePushDataLogger(GyroDataAutoGen::new);
        WPI_PigeonIMU pigeonIMU = new WPI_PigeonIMU(canId);

        pigeonIMU.calibrate(); //calibrate on spawn

        return new GyroControl(pigeonIMU);
    }
}
