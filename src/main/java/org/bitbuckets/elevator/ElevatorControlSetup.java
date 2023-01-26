package org.bitbuckets.elevator;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

public class ElevatorControlSetup implements ISetup<ElevatorControl> {

        final ISetup<IMotorController> leftExtend;
        final ISetup<IMotorController> rightExtend;
        final ISetup<IMotorController> leftTilt;
        final ISetup<IMotorController>rightTilt;

    
    public ElevatorControlSetup(ISetup<IMotorController> leftExtend, ISetup<IMotorController> rightExtend, ISetup<IMotorController> leftTilt, ISetup<IMotorController> rightTilt) {
        this.leftExtend = leftExtend;
        this.rightExtend = rightExtend;

        this.leftTilt = leftTilt;
        this.rightTilt = rightTilt;


    }


    @Override
    public ElevatorControl build(ProcessPath path) {

        ElevatorControl control = new ElevatorControl(
                leftExtend.build(path.addChild("elevator-left-extension")),
                rightExtend.build(path.addChild("elevator-right-extension")),
                leftTilt.build(path.addChild("elevator-left-tilt")),
                rightTilt.build(path.addChild("elevator-right-tilt"))


        );

        return control;
    }
}
