package org.bitbuckets.lib.core;

import org.bitbuckets.lib.tune.IValueTuner;

import java.util.ArrayList;

public class ProcessDriver implements IProcessDriver {

    @Override
    public int rootProcess(String name) {


        return 0;
    }

    @Override
    public int childProcess(int parentId, String name) {


        return 0;
    }

    @Override
    public String getRootNameOfProcess(int id) {
        return null;
    }

    @Override
    public String getRootNamePlusOtherName(int id, String substring) {

        return null;
    }

    @Override
    public boolean isProcessEnabled(int id) {
        if (id >= tunerArray.size())  throw new IllegalStateException("Process ID is too big!");
        IValueTuner<Boolean> tuner = tunerArray.get(id);

        return tuner.readValue();
    }

    //this is stupid
    final ArrayList<IValueTuner<Boolean>> tunerArray = new ArrayList<>();

}
