package org.bitbuckets.elevator;

import org.bitbuckets.lib.hardware.IMotorController;

public class ElevatorControl {

    private IMotorController leftExtend;
    private IMotorController rightExtend;
    private IMotorController leftTilt;
    private IMotorController rightTilt;

    public ElevatorControl(IMotorController leftExtend, IMotorController rightExtend, IMotorController leftTilt, IMotorController rightTilt) {
        this.leftExtend = leftExtend;
        this.rightExtend = rightExtend;
        this.leftTilt  = leftTilt;
        this.rightTilt = rightTilt;

        leftExtend.follow(2,false);
        leftTilt.follow(4,false);


    }

    public void extendUp()
    {
        rightExtend.setInverted(false);
        rightExtend.moveAtPercent(20);
    }
    public void extendDown()
    {
        rightExtend.setInverted(true);
        rightExtend.moveAtPercent(20);
    }
    public void tiltLeft()
    {
        rightTilt.setInverted(false);
        rightTilt.moveAtPercent(20);
    }
    public void tiltRight()
    {
        rightTilt.setInverted(true);
        rightTilt.moveAtPercent(20);    }




}
