package org.bitbuckets.lib.id;

import org.bitbuckets.lib.core.IdentityDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentityDriverTest {

    @Test
    public void shouldHaveRoot() {
        IdentityDriver manager = new IdentityDriver(); //why was this mocked lmao

        Assertions.assertEquals("root/", manager.fullPath(0));
    }

    @Test
    public void shouldDoParents() {
        IdentityDriver identityDriver = new IdentityDriver();

        int id = identityDriver.childProcess(0, "hello");
        Assertions.assertEquals("root/hello/", identityDriver.fullPath(id));
    }


}
