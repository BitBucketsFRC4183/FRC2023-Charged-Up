package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.hardware.IGyroLogger;
import org.bitbuckets.odometry.PidgeonOdometryControl;

public class PidgeonGyroSetup implements ISetup<IGyro> {

    final int pidgeonId;
    final Pigeon2.AxisDirection forward; //y
    final Pigeon2.AxisDirection up; //z


    public PidgeonGyroSetup(int pidgeonId, Pigeon2.AxisDirection forward, Pigeon2.AxisDirection up) {
        this.pidgeonId = pidgeonId;
        this.forward = forward;
        this.up = up;
    }

    @Override
    public IGyro build(IProcess self) {

        WPI_Pigeon2 pigeonIMU = new WPI_Pigeon2(pidgeonId);
        pigeonIMU.configFactoryDefault();
        pigeonIMU.configMountPose(forward, up);

        PidgeonGyro gyro = new PidgeonGyro(
                pigeonIMU
        );

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
