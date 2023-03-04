package org.bitbuckets.lib.core;

import org.bitbuckets.auto.AutoFSM;

@AutoTrait
public interface HasLifecycle {

    void onEvent(String autoEvent);
    void onPhaseChangeEvent(AutoFSM phase);

}
