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

    double endEffectorMagnitude = 1;

    double x1 = 0;
    double theta1 = 0;

    ElevatorConstants elevatorConstants = new ElevatorConstants();

    public void smartDashboard()
    {
        SmartDashboard.putNumber("extendEncoderLeft", leftExtend.getPositionMechanism_meters());
        SmartDashboard.putNumber("extendEncoderRight", rightExtend.getPositionMechanism_meters());
        SmartDashboard.putNumber("tiltEncoderLeft", Math.toDegrees(leftTilt.getMechanismPositionAccum_rot()* 2.0 * Math.PI));
        SmartDashboard.putNumber("tiltEncoderRight", Math.toDegrees(rightTilt.getMechanismPositionAccum_rot()* 2.0 * Math.PI));
    }




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
        elevator.setAngle(90-Math.toDegrees(leftTilt.getMechanismPositionAccum_rot() * 2.0 * Math.PI));
        elevatorWrist.setAngle(90);


    }

    public void setElevatorMech2dIK()
    {
        SmartDashboard.putNumber("extensionX1",x1);
        SmartDashboard.putNumber("angleTheta1",theta1);
        elevator.setLength(x1);
        elevator.setAngle(theta1);

        elevatorWrist.setAngle(90);


    }
    public void setElevatorActualIK()
    {
        SmartDashboard.putNumber("extensionX1",x1);
        SmartDashboard.putNumber("angleTheta1",theta1);
        leftExtend.moveToPosition(x1*elevatorConstants.getGearRatioExtend);
        leftTilt.moveToPosition(theta1*elevatorConstants.gearRatioTilt  );


    }

    public void goToPosition(double phi, double r)
    {
        double x = Math.sqrt(r*r) - endEffectorMagnitude*endEffectorMagnitude;
        double theta = phi - Math.atan(endEffectorMagnitude/(Math.sqrt(x)));
        x1 = x;
        theta1 = theta;


    }

    public void gotoPositionButton()
    {
        goToPosition(60,2);
        setElevatorMech2dIK();


    }


    public void extendUp()
    {
        rightExtend.moveAtPercent(0.2);
        leftExtend.moveAtPercent(0.2);



    }
    public void extendDown()
    {

        rightExtend.moveAtPercent(-0.2);
        leftExtend.moveAtPercent(-0.2);
    }
    public void tiltForward()
    {

        rightTilt.moveAtPercent(0.2);
        leftTilt.moveAtPercent(0.2);
    }
    public void tiltBack()
    {

        rightTilt.moveAtPercent(-0.2);
        leftTilt.moveAtPercent(-0.2);
    }
    public void stopTilt()
    {

        rightTilt.moveAtPercent(0.0);
        leftTilt.moveAtPercent(-0.0);
    }
    public void stopExtend()
    {

        leftExtend.moveAtPercent(0.0);
        rightExtend.moveAtPercent(-0.0);
    }



}
