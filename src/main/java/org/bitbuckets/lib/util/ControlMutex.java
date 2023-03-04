package org.bitbuckets.lib.util;

import java.util.function.Consumer;

public interface ControlMutex<T> {

    int queueToHappenWhenPossible(Consumer<T> doSomethingWithT);

    boolean cancelIfNotHappenedYet(int id);

}
