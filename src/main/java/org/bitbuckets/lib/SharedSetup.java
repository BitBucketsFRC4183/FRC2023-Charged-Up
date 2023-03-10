package org.bitbuckets.lib;

//singleton
public class SharedSetup<T> implements ISetup<T> {

    T cached;

    final ISetup<T> delegate;

    public SharedSetup(ISetup<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T build(IProcess self) {
        if (cached == null) {
            cached = delegate.build(self);
        }

        return cached;
    }
}
