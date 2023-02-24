package org.bitbuckets.lib;

import java.util.function.Consumer;

public class LazyConsumer<T> implements Consumer<T> {

    Consumer<T> val;

    @Override
    public void accept(T t) {
        if (val != null) {
            val.accept(t);
        }
    }

    public void late(Consumer<T> set) {
        val = set;
    }
}
