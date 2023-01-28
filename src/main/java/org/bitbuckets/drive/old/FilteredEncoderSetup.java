package org.bitbuckets.drive.old;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IEncoder;
import org.bitbuckets.lib.SetupProfiler;

public class FilteredEncoderSetup implements ISetup<FilteredEncoder> {

    final double magneticOffset_degrees;
    final ISetup<IEncoder> relative;

    public FilteredEncoderSetup(double magneticOffset_degrees, ISetup<IEncoder> relative) {
        this.magneticOffset_degrees = magneticOffset_degrees;
        this.relative = relative;
    }

    double generateStartingPosition_encoderRadians(IEncoder encoder, double startingPosition_mechanismRadians) {
        return startingPosition_mechanismRadians / encoder.getMechanismFactor();
    }

    public double retrieveCanCoderReadout_mechanismRadians(CANCoder cancoder) {
        double lastAbsolutePosition = cancoder.getAbsolutePosition();
        double sanityCheckingCounter = 0;
        while (sanityCheckingCounter < 3) {
            double currentPosition = cancoder.getAbsolutePosition();

            if (lastAbsolutePosition == currentPosition) {
                sanityCheckingCounter++;
            }
            lastAbsolutePosition = currentPosition;
        }

        return lastAbsolutePosition;
    }

    void forcePositionToEncoder(IEncoder relativeEncoder, double lastAbsolutePosition_mechanismRadians) {
        double startingPosition_encoderRadians = generateStartingPosition_encoderRadians(relativeEncoder, lastAbsolutePosition_mechanismRadians);
        double encoderSensorUnits = startingPosition_encoderRadians / Math.PI / 2.0 * 2048.0;
        relativeEncoder.forceOffset(encoderSensorUnits);
    }

    //TODO test this all more
    @Override
    public FilteredEncoder build(ProcessPath path) {
        SetupProfiler initCancoder = path.generateSetupProfiler("init-cancoder");
        SetupProfiler forceRelative = path.generateSetupProfiler("force-relative");

        initCancoder.markProcessing();
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        config.sensorCoefficient = 2 * Math.PI / 4096.0;
        config.unitString = "rad";
        config.sensorTimeBase = SensorTimeBase.PerSecond;
        config.magnetOffsetDegrees = magneticOffset_degrees;
        config.enableOptimizations = true;

        CANCoder cancoder = new CANCoder(0);
        ErrorCode code = cancoder.configAllSettings(config, 250);
        if (code != null) {
            initCancoder.markErrored("cannot use can encoder for reading: bad encoder");
        }
        CANCoderFaults faults = new CANCoderFaults();
        cancoder.getFaults(faults);

        if (faults.hasAnyFault()) {
            initCancoder.markErrored("should not have any faults");
        }
        initCancoder.markCompleted();

        forceRelative.markProcessing();
        double lastAbsolutePosition = retrieveCanCoderReadout_mechanismRadians(cancoder);
        IEncoder relativeEncoder = relative.build(path);
        forcePositionToEncoder(relativeEncoder, lastAbsolutePosition);
        forceRelative.markCompleted();

        return new FilteredEncoder(relativeEncoder);
    }
}
