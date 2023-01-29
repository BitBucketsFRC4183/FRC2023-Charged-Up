package org.bitbuckets.lib.log;

import org.bitbuckets.lib.core.LogDriver;

import java.util.function.Consumer;

@Deprecated
public class DataLogger<T extends IDiffableData> {

    final int parentId;
    final LogDriver driver;

    T cachedData;

    public DataLogger(int parentId, LogDriver driver, T cachedData) {
        this.parentId = parentId;
        this.driver = driver;
        this.cachedData = cachedData;
    }


    /**
     * Call this as much as you want
     * It will optimize it's own bandwidth as it pleases
     * @param dataProcessor
     */
    public void process(Consumer<T> dataProcessor) {
        cachedData.startDiff();

        dataProcessor.accept(cachedData);

        cachedData.completeDiff(parentId, driver);
    }
}
