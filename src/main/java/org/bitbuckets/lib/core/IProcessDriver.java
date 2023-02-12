package org.bitbuckets.lib.core;

public interface IProcessDriver {

    int rootProcess(String name);
    int childProcess(int parentId, String name);

    String getRootNameOfProcess(int id);
    String getRootNamePlusOtherName(int id, String substring);

    boolean isProcessEnabled(int id);

}
