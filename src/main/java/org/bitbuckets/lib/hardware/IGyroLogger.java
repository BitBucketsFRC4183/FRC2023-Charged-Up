package org.bitbuckets.lib.hardware;

import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.ILoggable;

public class IGyroLogger implements HasLogLoop {

    final IGyro gyro;

    final ILoggable<Double> yaw;
    final ILoggable<Double> pitch;
    final ILoggable<Double> roll;

    public IGyroLogger(IGyro gyro, ILoggable<Double> yaw, ILoggable<Double> pitch, ILoggable<Double> roll) {
        this.gyro = gyro;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    @Override
    public void logLoop() {
        yaw.log(gyro.getAllianceRelativeYaw_deg());
        pitch.log(gyro.getAllianceRelativePitch_deg());
        roll.log(gyro.getAllianceRelativeRoll_deg());
    }

}
