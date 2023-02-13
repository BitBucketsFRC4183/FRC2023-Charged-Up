package org.bitbuckets.lib.process;

import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.console.IConsole;

import java.util.function.Supplier;

public interface IProcessDriver {

    int rootProcess(String name);
    int childProcess(int parentId, String name);

    Path getPath(int id);

    Supplier<Boolean> getProcessEnabled(int id);
    IConsole getConsole(int id);
    <T> ILoggable<T> getLoggable(int id);

}
