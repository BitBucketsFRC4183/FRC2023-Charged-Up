package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import edu.wpi.first.wpilibj.DriverStation;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.log.ILoggable;

public class CANCoderAbsoluteSetup implements ISetup<IAbsoluteEncoder> {
    final int canId;
    final double offset;

    final int periodMilliseconds = 10;
    final Direction direction = Direction.COUNTER_CLOCKWISE;

    public CANCoderAbsoluteSetup(int canId, double offset) {
        this.canId = canId;
        this.offset = offset;
    }

    public enum Direction {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }

    @Override
    public IAbsoluteEncoder build(IProcess self) {
        WPI_CANCoder encoder = new WPI_CANCoder(canId);

        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.magnetOffsetDegrees = Math.toDegrees(offset);
        config.sensorDirection = direction == Direction.CLOCKWISE;
        ErrorCode errorCode1 = encoder.configAllSettings(config, 250);
        if (errorCode1 != ErrorCode.OK) {
            DriverStation.reportError(String.format("%s: %s", "Failed to configure CANCoder", errorCode1.toString()), false);
        }
        ErrorCode errorCode = encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, periodMilliseconds, 250);
        if (errorCode != ErrorCode.OK) {
            DriverStation.reportError(String.format("%s: %s", "Failed to configure CANCoder update rate", errorCode.toString()), false);
        }

        ILoggable<Double> raw = self.generateLogger(ILogAs.DOUBLE, "raw-angle-degrees");
        ILoggable<Double> ang = self.generateLogger(ILogAs.DOUBLE, "angle-degrees");

        return new CANCoderAbsolute(encoder, raw, ang);
    }
}
