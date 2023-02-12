package org.bitbuckets.lib.log;

public class ModernizeLoggable implements ILoggable<String> {

    final int identity;
    final String fullPath;

    public ModernizeLoggable(int identity, String fullPath) {
        this.identity = identity;
        this.fullPath = fullPath;
    }

    @Override
    public void log(String data) { //check if this identity is enabled
        //TODO somehow determine if data is stale or not

    }
}
