package org.bitbuckets.lib;

import edu.wpi.first.hal.HALUtil;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.tune.IValueTuner;

public interface IProcess {

    default boolean isReal() {
        return HALUtil.getHALRuntimeType() != 2;
    }

    Path getSelfPath();

    @Deprecated
    IProcess child(String key);
    <T> T childSetup(String key, ISetup<T> setup);
    <T> T inlineSetup(ISetup<T> setup);

    IConsole getAssociatedConsole();
    IDebuggable getDebuggable();

    <T> IValueTuner<T> generateTuner(Class<T> tuneDataType, String key, T dataWhenNotTuning);
    <T> ILoggable<T> generateLogger(Class<T> logDataType, String key);


    //IActionProfiler generateProfiler(String key);
    //IActionProfiler generateStartupProfiler(String key);

    void registerLogLoop(HasLogLoop loop);
    void registerLogicLoop(HasLoop loop);

    void run();

}
