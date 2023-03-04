package org.bitbuckets.lib.core;

import org.bitbuckets.lib.log.ILoggable;

public interface OpenAndClose<T> extends ILoggable<T> {

    void open();
    void close();

}
