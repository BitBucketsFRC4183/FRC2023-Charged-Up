package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;

public interface IArmControl {

    Mechanism2d robotArmRootNode();
    Mechanism2d robotArmLigamentNode();
    Mechanism2d robotArmJointNode();

}
