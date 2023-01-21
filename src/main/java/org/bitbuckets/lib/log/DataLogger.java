package org.bitbuckets.lib.log;

import org.bitbuckets.lib.core.LogDriver;

import java.util.function.Consumer;

public class DataLogger<T extends IDiffableData> {

    final int parentId;
    final LogDriver driver;

    T cachedData;

    public DataLogger(int parentId, LogDriver driver, T cachedData) {
        this.parentId = parentId;
        this.driver = driver;
        this.cachedData = cachedData;
    }


    public void process(Consumer<T> dataProcessor) {
        cachedData.startDiff();

        dataProcessor.accept(cachedData);

        cachedData.completeDiff(parentId, driver);
    }
}
