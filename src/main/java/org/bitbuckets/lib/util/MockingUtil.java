package org.bitbuckets.lib.util;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import org.bitbuckets.lib.ISetup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Purpose of this class is to be able to generate instances of any class
 * that require no dependencies and do nothing.
 */
public class MockingUtil {

    final TypeCache<?> cache = new TypeCache<>();


    public static <T> ISetup<T> noops(Class<T> clazz) {
        return a -> buddy(clazz);
    }


    static class SelfHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getReturnType().equals(Void.TYPE)) return null;
            if (method.getReturnType().equals(Optional.class)) return Optional.empty();


            //System.out.println("invocation of: " + method.getName());

            throw new UnsupportedOperationException("Tried to call method with return: " + method.getName() + " but it was on a mocked object!");
        }
    }

    /**
     * You can only use this on public classes, otherwise it will break
     * @param copy
     * @return a dynamic generated version of your class that will do nothing and return 0 or null wheenver it is called
     * @param <T> type of class you want
     */

    public static <T> T buddy(Class<T> copy) {
        if (copy.isInterface()) {
            //TODO return dynamic proxy

            return (T) Proxy.newProxyInstance(
                    MockingUtil.class.getClassLoader(),
                    new Class[] { copy },
                    new SelfHandler());

        }

        Constructor<?> [] constructor = copy.getConstructors();
        if (constructor.length == 0) throw new IllegalStateException();


        int len = constructor[0].getParameterCount();
        Object[] nulls = new Object[len];



        DynamicType.Unloaded<T> buddy = new ByteBuddy()
                .subclass(copy, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineConstructor(Visibility.PUBLIC)
                .intercept(MethodCall.invoke(constructor[0])
                        .with(nulls)
                        )
                .method(any())
                .intercept(StubMethod.INSTANCE)
                .make();


        try {
            Class<?> clazz =  buddy.load(MockingUtil.class.getClassLoader()).getLoaded();

            return (T) clazz.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
