package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.sensors.WPI_Pigeon2;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.hardware.IGyroLogger;
import org.bitbuckets.lib.log.IDebuggable;

public class PigeonGyroSetup implements ISetup<IGyro> {

    final int pidgeonId;
    final Pigeon2.AxisDirection forward; //y
    final Pigeon2.AxisDirection up; //z


    public PigeonGyroSetup(int pidgeonId, Pigeon2.AxisDirection forward, Pigeon2.AxisDirection up) {
        this.pidgeonId = pidgeonId;
        this.forward = forward;
        this.up = up;
    }

    @Override
    public IGyro build(IProcess self) {

        System.out.println("EEEEEEJEOKEOKEOKOE");

        WPI_Pigeon2 pigeonIMU = new WPI_Pigeon2(pidgeonId);

        pigeonIMU.configFactoryDefault();
        pigeonIMU.configMountPose(forward, up);


        PigeonGyro gyro = new PigeonGyro(
                pigeonIMU,
                self.getDebuggable());

        System.out.println("EEEEEEEE");

        IGyroLogger logger = new IGyroLogger(
                gyro,
                self.generateLogger(ILogAs.DOUBLE, "yaw"),
                self.generateLogger(ILogAs.DOUBLE, "pitch"),
                self.generateLogger(ILogAs.DOUBLE, "roll")
        );

        self.registerLogLoop(logger);


        System.out.println("EEEEEEEEE");

        return gyro;
    }
}
