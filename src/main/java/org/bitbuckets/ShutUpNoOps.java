package org.bitbuckets;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.StubMethod;
import org.bitbuckets.lib.DontUseIncubating;

import static net.bytebuddy.matcher.ElementMatchers.any;

@Deprecated @DontUseIncubating
public class ShutUpNoOps {

    final TypeCache<?> cache = new TypeCache<>();


    @Deprecated @DontUseIncubating
    public <T> T buddy(Class<T> copy) {

        DynamicType.Unloaded<T> buddy = new ByteBuddy()
                .subclass(copy)
                .method(any())
                .intercept(StubMethod.INSTANCE)
                .make();

        try {
            return buddy.load(copy.getClassLoader()).getLoaded().newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
