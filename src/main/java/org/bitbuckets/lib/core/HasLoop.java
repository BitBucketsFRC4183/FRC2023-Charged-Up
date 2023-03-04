package org.bitbuckets.lib.core;

import java.io.Serializable;

@AutoTrait
public interface HasLoop extends Serializable { //dont remove serializable

    void loop();

}
