package org.bitbuckets.lib;

import edu.wpi.first.hal.HALUtil;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

public interface IProcess {

    default boolean isReal() {
        return HALUtil.getHALRuntimeType() != 2;
    }

    Path getSelfPath();


    //childReference
    <T> T childSetup(String key, ISetup<T> setup);
    <T> T siblingSetup(String key, ISetup<T> setup); //parents this child to our parent

    IConsole getAssociatedConsole();
    IDebuggable getDebuggable();

    <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning);
    <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key);



    void forceTo(ProcessMode mode);

    void registerLogLoop(HasLogLoop loop);
    void registerLogicLoop(HasLoop loop);
    void registerLifecycle(HasLifecycle lifecycle);

    //dont call this
    void run();

    //uses for breaking shit
    int componentQuantity();

    //dont call this
    HasLifecycle offerInternalLifecycler();

}
