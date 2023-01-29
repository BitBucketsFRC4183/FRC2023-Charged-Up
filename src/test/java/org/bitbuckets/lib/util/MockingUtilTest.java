package org.bitbuckets.lib.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MockingUtilTest {

    public static class StubClass {
        public int hello() {
            return 5;
        }
    }


    @Test
    public void shouldMock() {
        StubClass stubClass = MockingUtil.buddy(StubClass.class);

        Assertions.assertEquals(0, stubClass.hello());
    }

}