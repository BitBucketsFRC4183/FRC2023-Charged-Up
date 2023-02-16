package org.bitbuckets.lib.log;

public interface IActionProfiler {

    void markStarted();
    void markErrored(Exception e);
    void markFinished();

}
