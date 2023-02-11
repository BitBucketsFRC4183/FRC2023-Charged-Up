package org.bitbuckets.lib.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngleUtilTest {

    @Test
    void wrapRotations() {
        double initialRotations = 0.5; //half a rotation
        double after = AngleUtil.wrapRotations(initialRotations);

        Assertions.assertEquals(0.5, after);


        double initialRotations2 = 1.5; //one and a half
        double after2 = AngleUtil.wrapRotations(initialRotations2);

        Assertions.assertEquals(0.5, after2);

        double initialRotations3 = -1.5; //-1.5
        double after3 = AngleUtil.wrapRotations(initialRotations3);

        Assertions.assertEquals(0.5, after3);
    }
}