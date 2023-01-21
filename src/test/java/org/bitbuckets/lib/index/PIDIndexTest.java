package org.bitbuckets.lib.index;

import org.bitbuckets.lib.hardware.PIDIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PIDIndexTest {

    @Test
    void CONSTANTS_shouldGenerateProperlyIndexed() {

        double[] constants = PIDIndex.CONSTANTS(1,0.1,0.1,0,1);

        Assertions.assertEquals(1, constants[PIDIndex.P]);
        Assertions.assertEquals(0.1, constants[PIDIndex.I]);
        Assertions.assertEquals(0.1, constants[PIDIndex.D]);
        Assertions.assertEquals(0, constants[PIDIndex.FF]);
        Assertions.assertEquals(1, constants[PIDIndex.IZONE]);

    }
}