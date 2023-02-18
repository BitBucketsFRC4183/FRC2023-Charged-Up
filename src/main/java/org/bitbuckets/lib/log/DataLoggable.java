package org.bitbuckets.lib.log;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;
import org.bitbuckets.lib.log.ILoggable;

//no matter what, send data to a data hole
public class DataLoggable<T> implements ILoggable<T> {

    final ILoggable<T> delegate;

    public DataLoggable(ILoggable<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void log(T data) {
    }
}
