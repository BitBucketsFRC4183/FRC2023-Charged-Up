package org.bitbuckets.lib.util;

import org.junit.jupiter.api.Assertions;

class MockingUtilTest {

    class StubClass {
        public int hello() {
            return 5;
        }
    }


    public void shouldMock() {
        StubClass stubClass = MockingUtil.buddy(StubClass.class);

        Assertions.assertEquals(0, stubClass.hello());
    }

}