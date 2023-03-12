package org.bitbuckets.lib.core;

import org.bitbuckets.auto.RobotEvent;

@AutoTrait
public interface HasLifecycle {

    void onPhaseChangeEvent(RobotEvent robotEvent);

}
