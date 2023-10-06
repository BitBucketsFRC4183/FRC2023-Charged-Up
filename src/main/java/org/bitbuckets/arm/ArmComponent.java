package org.bitbuckets.arm;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;

public interface ArmComponent {

    default float errorTolerance_rotations() {
        return 0.01f; //of a rotation
    }

    default float manualModeThresholdToGoToManual() {
        return 0.05f;
    }


    default Vector<N2> storePosition() {
        return VecBuilder.fill(0.19, -0.4);
    }

    default Vector<N2> loadPosition() {
        return VecBuilder.fill(0.008, -0.25);
    }

    default Vector<N2> humanIntakePosition() {
        return VecBuilder.fill(0.008, -0.230);
    }

    default Vector<N2> groundIntakePosition() {
        return VecBuilder.fill(0.581, -0.274);
    }

    default Vector<N2> unstowPosition() {
        return VecBuilder.fill(-0.1, 0.2);
    }

    default Vector<N2> scoreHighPosition() {
        return VecBuilder.fill(-0.126, 0.025);
    }
    default Vector<N2> scoreMidPosition() {
        return VecBuilder.fill(0.008, -0.227);
    }

    void logSomethin(double d);



}
