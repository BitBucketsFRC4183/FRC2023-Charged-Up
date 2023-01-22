package org.bitbuckets;

public enum SimLevel {

    NONE,
    SIM_CLASSES, //When this is enabled, the alternate sim classes are used if you use ISetup splitters. Preferred.
    SIM_MOTORS //When this is enabled, your code runs as normal but all motors will be simulated using
    //WPILib's simulation combined with some MattLib stuff

}
