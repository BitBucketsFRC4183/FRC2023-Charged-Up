package org.bitbuckets.lib.util;

import java.util.function.Supplier;

public class LateSupplier<T> implements Supplier<T> {

    Supplier<T> supplier;

    public void set(Supplier<T> supplier) {
        this.supplier = supplier;
    }


    @Override
    public T get() {
        if (supplier == null) throw new IllegalStateException("not set yet");

        return supplier.get();
    }
}
