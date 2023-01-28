package org.bitbuckets.lib.log;

import org.bitbuckets.lib.core.LogDriver;

//this is implemented by my annotation generator which will dynamically figure out what to do
@Deprecated
public interface IDiffableData {

    /**
     * This method should shift all values from current fields to auto-generated fields
     * representing past values
     */
    void startDiff();

    /**
     * this method should diff past value fields to current fields with auto generated code
     * it assumes the current fields have been set with new values by a unary operator
     *
     * @param parentId
     * @param driver
     */
    void completeDiff(int parentId, LogDriver driver);


}
