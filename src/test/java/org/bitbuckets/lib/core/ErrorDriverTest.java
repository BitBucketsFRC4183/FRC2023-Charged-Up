package org.bitbuckets.lib.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorDriverTest {

    @Test
    public void ifErrorDriver_thenDoStuff() {
        IdentityDriver driver = new IdentityDriver();
        ErrorDriver tested = new ErrorDriver(driver);

        assertThrows(IllegalStateException.class, () -> {
            tested.flagError(0, "some error");
        });
    }

}