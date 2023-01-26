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




    }

    public void extendUp()
    {
        rightExtend.moveAtPercent(20);
    }
    public void extendDown()
    {
        rightExtend.moveAtPercent(-20);
    }
    public void tiltForward()
    {
        rightTilt.moveAtPercent(20);
    }
    public void tiltBack()
    {
        rightTilt.moveAtPercent(-20);
    }




}
