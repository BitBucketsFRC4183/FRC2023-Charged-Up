package org.bitbuckets.lib;

import org.bitbuckets.lib.util.MockingUtil;

public class ToggleableSetup<T> implements ISetup<T> {

    final boolean enabled;
    final Class<T> type;
    final ISetup<T> delegate;

    public ToggleableSetup(boolean enabled, Class<T> type, ISetup<T> delegate) {
        this.enabled = enabled;
        this.type = type;
        this.delegate = delegate;
    }

    @Override
    public T build(IProcess self) {
        if (!enabled) {
            return MockingUtil.buddy(type);
        }

        return delegate.build(self);
    }
}
