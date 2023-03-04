package org.bitbuckets.lib.core;

import java.io.Serializable;

@AutoTrait
public interface HasLogLoop extends Serializable {

    void logLoop();

}
