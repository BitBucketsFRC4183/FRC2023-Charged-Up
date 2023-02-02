package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;

public class CANCoderAbsoluteEncoderSetup implements ISetup<IAbsoluteEncoder> {
    final int canId;
    final double offset;

    final int periodMilliseconds = 10;
    final Direction direction = Direction.COUNTER_CLOCKWISE;

    public CANCoderAbsoluteEncoderSetup(int canId, double offset) {
        this.canId = canId;
        this.offset = offset;
    }

    public enum Direction {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }

    @Override
    public IAbsoluteEncoder build(ProcessPath path) {
        WPI_CANCoder encoder = new WPI_CANCoder(canId);

        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.magnetOffsetDegrees = Math.toDegrees(offset);
        config.sensorDirection = direction == Direction.CLOCKWISE;
        CtreUtils.checkCtreError(encoder.configAllSettings(config, 250), "Failed to configure CANCoder");
        CtreUtils.checkCtreError(encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, periodMilliseconds, 250), "Failed to configure CANCoder update rate");

        return new CANCoderAbsoluteEncoder(encoder);
    }
}
