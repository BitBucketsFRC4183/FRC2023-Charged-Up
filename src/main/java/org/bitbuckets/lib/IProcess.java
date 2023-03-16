package org.bitbuckets.lib;

import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

public interface IProcess extends HasLifecycle, HasLoop, HasLogLoop {

    boolean isReal();
    Path getSelfPath();


    //childReference
    <T> T childSetup(String key, ISetup<T> setup);
    <T> T siblingSetup(String key, ISetup<T> setup); //parents this child to our parent

    IDebuggable getDebuggable();

    <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning);
    <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key);

    void registerLogLoop(HasLogLoop loop);
    void registerLogicLoop(HasLoop loop);
    void registerLifecycle(HasLifecycle lifecycle);



}
