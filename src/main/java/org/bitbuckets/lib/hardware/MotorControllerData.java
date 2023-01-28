package org.bitbuckets.lib.hardware;

import org.bitbuckets.lib.log.Loggable;
import org.bitbuckets.lib.log.LoggableKey;

@Loggable
public class MotorControllerData {

    @LoggableKey("reset")
    public boolean isResetting = false;

    public double setpoint_encoderSU = 0.0; //can be null
    public double read_SU = 0.0;
    public double read_encoderAccumRads = 0.0;
    public double read_encoderBoundRads = 0.0;
    public double read_mechAccumRads = 0.0;
    public double read_mechBoundRads = 0.0;

    public double busVoltage = 0.0;
    public double temp = 0.0;

    public double someCoolNumber = 0.0;

}