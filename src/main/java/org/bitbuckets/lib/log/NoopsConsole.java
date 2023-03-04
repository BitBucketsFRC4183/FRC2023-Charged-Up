package org.bitbuckets.lib.log;

public class NoopsConsole implements IConsole{
    @Override
    public void sendInfo(String data) {

    }

    @Override
    public void sendError(Exception exception) {

    }

    @Override
    public void sendWarning(String data) {

    }
}
