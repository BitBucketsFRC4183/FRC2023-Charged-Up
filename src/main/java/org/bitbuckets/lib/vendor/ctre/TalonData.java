package org.bitbuckets.lib.vendor.ctre;

import org.bitbuckets.lib.log.Loggable;
import org.bitbuckets.lib.log.LoggableKey;

@Loggable
public class TalonData {

    boolean isResetting;

    double setpoint_encoderSU = 0.0; //can be null
    double read_SU = 0.0;
    double read_encoderAccumRads = 0.0;
    double read_encoderBoundRads = 0.0;
    double read_mechAccumRads = 0.0;
    double read_mechBoundRads = 0.0;

    double busVoltage = 0.0;
    double temp = 0.0;

}
