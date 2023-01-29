package org.bitbuckets.lib.util;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.StubMethod;
import org.bitbuckets.lib.DontUseIncubating;
import org.bitbuckets.lib.ISetup;

import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Purpose of this class is to be able to generate instances of any class
 * that require no dependencies and do nothing.
 */
@Deprecated @DontUseIncubating
public class MockingUtil {

    final TypeCache<?> cache = new TypeCache<>();

    @Deprecated
    public static <T> ISetup<T> noops(Class<T> clazz) {
        return a -> buddy(clazz);
    }


    /**
     * You can only use this on public classes, otherwise it will break
     * @param copy
     * @return a dynamic generated version of your class that will do nothing and return 0 or null wheenver it is called
     * @param <T> type of class you want
     */
    @Deprecated @DontUseIncubating
    public static <T> T buddy(Class<T> copy) {

        DynamicType.Unloaded<T> buddy = new ByteBuddy()
                .subclass(copy)
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
