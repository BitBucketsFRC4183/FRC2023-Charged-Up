package org.bitbuckets.lib.abstractions;

import org.bitbuckets.lib.hardware.IRaw;

public interface ILowLevelEncoder extends IRaw {

    double getRawPosition_baseUnits();
    double getRawVelocity_baseUnitsBaseTime();
    void forceOffset(double offsetUnits_baseUnits);


}
