package org.bitbuckets.lib.vendor.navx;

import com.kauailabs.navx.frc.AHRS;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.hardware.IGyroLogger;

import static edu.wpi.first.wpilibj.SPI.Port.kMXP;

public class NavxGyroSetup implements ISetup<IGyro> {


    @Override
    public IGyro build(IProcess self) {
        NavxGyro gyro = new NavxGyro(new AHRS(kMXP, (byte) 200));

        IGyroLogger logger = new IGyroLogger(
                gyro,
                self.generateLogger(ILogAs.DOUBLE, "yaw"),
                self.generateLogger(ILogAs.DOUBLE, "pitch"),
                self.generateLogger(ILogAs.DOUBLE, "roll")
        );

        self.registerLogLoop(logger);

        return gyro;
    }
}
