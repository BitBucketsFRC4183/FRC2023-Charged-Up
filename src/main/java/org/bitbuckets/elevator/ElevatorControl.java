package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.bitbuckets.lib.hardware.IMotorController;

public class ElevatorControl {

    private IMotorController leftExtend;
    private IMotorController rightExtend;
    private IMotorController leftTilt;
    private IMotorController rightTilt;

    private MechanismLigament2d elevator;
    private MechanismLigament2d elevatorWrist;

    private static final double elevatorMinLength   = 2;
    final double elevatorAngle = 90;



    public ElevatorControl(IMotorController leftExtend, IMotorController rightExtend, IMotorController leftTilt, IMotorController rightTilt, MechanismLigament2d elevator, MechanismLigament2d elevatorWrist) {
        this.leftExtend = leftExtend;
        this.rightExtend = rightExtend;
        this.leftTilt  = leftTilt;
        this.rightTilt = rightTilt;
        this.elevatorWrist = elevatorWrist;
        this.elevator = elevator;




    }

    public void setElevatorMech2d()
    {
        elevator.setLength(elevatorMinLength + leftExtend.getPositionMechanism_meters());
        elevator.setAngle(90-Math.toDegrees(leftTilt.getMechanismPositionAccumulated_radians()));
        elevatorWrist.setAngle(90);

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
