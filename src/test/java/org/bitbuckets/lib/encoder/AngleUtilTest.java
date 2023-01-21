package org.bitbuckets.lib.encoder;

import org.bitbuckets.lib.util.AngleUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AngleUtilTest {

    @Test
    public void wrap_shouldWorkWithConvDegrees() {
        Assertions.assertEquals(Math.toRadians(30), AngleUtil.wrap(Math.toRadians(30)), 0.0001);
        Assertions.assertEquals(Math.toRadians(30), AngleUtil.wrap(Math.toRadians(390)),0.0001);
        Assertions.assertEquals(Math.toRadians(330), AngleUtil.wrap(Math.toRadians(-390)),0.0001);
    }

    @Test
    public void wrap_shouldWorkWithPureRadians() {
        Assertions.assertEquals(1 * Math.PI, AngleUtil.wrap(3 * Math.PI));
        Assertions.assertEquals(Math.PI, AngleUtil.wrap(-3 * Math.PI));
        Assertions.assertEquals(Math.toRadians(359), AngleUtil.wrap(Math.toRadians(-1)));
    }


}
