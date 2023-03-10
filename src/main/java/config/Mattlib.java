package config;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ProcessMode;

public interface Mattlib {

    ProcessMode DEFAULT_MODE = ProcessMode.LOG_DEBUG;
    boolean SHOULD_FORCE_KILL = false;

}
