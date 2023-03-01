package org.bitbuckets.lib.log;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.DataLog;

public class ValueLoggable<T> implements ILoggable<T> {

    final GenericEntry entry;
    final DataLog log;

    public ValueLoggable(GenericEntry entry, DataLog log) {
        this.entry = entry;
        this.log = log;
    }

    @Override
    public void log(T data) {

    }
}
