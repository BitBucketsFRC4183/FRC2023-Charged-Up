package org.bitbuckets.lib.util;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TurnOffUtil implements ISetup<Object> {


    @Override
    public Object build(ProcessPath path) {
        return null;
    }

    class BucketProxy implements InvocationHandler {


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            method.getReturnType();

            return null;
        }
    }





}
