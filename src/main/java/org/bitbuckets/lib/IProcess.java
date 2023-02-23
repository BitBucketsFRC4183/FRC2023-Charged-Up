package org.bitbuckets.lib;

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.util.sendable.Sendable;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.HasLogLoop;
import org.bitbuckets.lib.util.HasLoop;

public interface IProcess {

    default boolean isReal() {
        return HALUtil.getHALRuntimeType() != 2;
    }

    Path getSelfPath();

    <T> T childSetup(String key, ISetup<T> setup);
    <T> T inlineSetup(ISetup<T> setup);

    IConsole getAssociatedConsole();
    IDebuggable getDebuggable();

    <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning);
    <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key);
    //void generateSendableHook(Sendable sendable);


    //IActionProfiler generateProfiler(String key);
    //IActionProfiler generateStartupProfiler(String key);

    void registerLogLoop(HasLogLoop loop);
    void registerLogicLoop(HasLoop loop);

    void run();

    //uses for breaking shit
    int componentQuantity();

}
