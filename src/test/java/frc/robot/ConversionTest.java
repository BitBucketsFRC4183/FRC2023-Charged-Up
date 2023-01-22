package frc.robot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConversionTest {

    @Test
    public void test() {

        double weirdRadianDegrees = Math.toDegrees(-Math.toRadians(255.49));
        double normieDegreees = -255.49;

        Assertions.assertEquals(normieDegreees, weirdRadianDegrees, 0.01);
    }

}
