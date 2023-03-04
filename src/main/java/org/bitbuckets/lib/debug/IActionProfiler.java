package org.bitbuckets.lib.debug;

public interface IActionProfiler {

    void markStarted();
    void markErrored(Exception e);
    void markFinished();

}
