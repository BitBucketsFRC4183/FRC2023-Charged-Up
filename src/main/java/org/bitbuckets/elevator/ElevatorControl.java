package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.hardware.IMotorController;

public class ElevatorControl {

    private IMotorController leftExtend;
    private IMotorController rightExtend;
    private IMotorController leftTilt;
    private IMotorController rightTilt;

    private MechanismLigament2d elevator;
    private MechanismLigament2d elevatorWrist;


    private static final double elevatorMinLength = 2;
    final double elevatorAngle = 90;

    double endEffectorMagnitude = 1.0;

    double x1 = 0;
    double theta1 = 0;

    ElevatorConstants elevatorConstants = new ElevatorConstants();

    public void smartDashboard() {
        SmartDashboard.putNumber("extendEncoderLeftRaw", leftExtend.getPositionRaw());
        SmartDashboard.putNumber("extendEncoderLeftMeters", leftExtend.getPositionMechanism_meters());
        //SmartDashboard.putNumber("extendEncoderRight", rightExtend.getPositionMechanism_meters());
        SmartDashboard.putNumber("tiltEncoderLeft", Math.toDegrees(leftTilt.getMechanismPositionAccum_rot() * 2.0 * Math.PI));
        //     SmartDashboard.putNumber("tiltEncoderRight", Math.toDegrees(rightTilt.getMechanismPositionAccum_rot() * 2.0 * Math.PI));
        SmartDashboard.putNumber("x1", x1);
        SmartDashboard.putNumber("extenROt", unitToRotExtend(x1));

        //   degrees = Math.toDegrees(leftTilt.getMechanismPositionAccum_rot() * 2.0 * Math.PI)

        //SmartDashboard.putNumber("output", leftExtend.rawAccess(CANSparkMax.class).getAppliedOutput());
        //SmartDashboard.putNumber("angleTheta1",theta1);
    }


    public ElevatorControl(IMotorController leftExtend, IMotorController leftTilt, MechanismLigament2d elevator, MechanismLigament2d elevatorWrist) {
        this.leftExtend = leftExtend;
        this.rightExtend = rightExtend;
        this.leftTilt = leftTilt;
        this.rightTilt = rightTilt;
        this.elevatorWrist = elevatorWrist;
        this.elevator = elevator;


    }


    public void zeroExtend() {
        leftExtend.forceOffset(0);
        //    rightExtend.forceOffset(0);
    }

    public void zeroTilt() {
        leftTilt.forceOffset(0);

        //   rightTilt.forceOffset(0);
    }

    public void setElevatorMech2d() {
        elevator.setLength(elevatorMinLength + leftExtend.getPositionMechanism_meters());
        elevator.setAngle(90 - Math.toDegrees(leftTilt.getMechanismPositionAccum_rot() * 2.0 * Math.PI));
        elevatorWrist.setAngle(90);


    }


    public void setElevatorActualIK() {

        // SmartDashboard.putNumber("pPPP", leftExtend.rawAccess(CANSparkMax.class).getPIDController().getP());
        // leftExtend.rawAccess(CANSparkMax.class).getPIDController().setP(0.05);
        SmartDashboard.putNumber("angleTheta1", theta1);

        double rot = unitToRotExtend(x1);
        SmartDashboard.putNumber("extendEncoderLeftSetpoint", rot);
        SmartDashboard.putNumber("extendEncoderLeftSetpointMeters", x1);

        SmartDashboard.putNumber("rotsw", rot);
        leftExtend.moveToPosition(rot);       // leftTilt.moveToPosition(theta1*elevatorConstants.gearRatioTilt  );
        leftTilt.moveToPosition(theta1 / 360.0 / ElevatorConstants.gearRatioTilt
        );


    }

    public double unitToRotExtend(double unit) {
        return unit / ElevatorConstants.rotToMeterExtend * ElevatorConstants.getGearRatioExtend;

    }

    public void goToPosition(double phi, double r) {

        double a = r * r;
        double b = Math.sqrt(a - endEffectorMagnitude * endEffectorMagnitude);
        double x = Math.sqrt(r * r - endEffectorMagnitude * endEffectorMagnitude);
        double theta = phi - Math.atan(endEffectorMagnitude / (Math.sqrt(x)));
        SmartDashboard.putNumber("x", x);


        x1 = x;
        theta1 = theta;


    }

    public void gotoPositionButton() {
        goToPosition(60.0, 1.3);
        // setElevatorMech2dIK();
        setElevatorActualIK();


    }


    public void extendUp() {
        //rightExtend.moveAtPercent(0.2);
        leftExtend.moveAtPercent(0.2);


    }

    public void extendDown() {

        // rightExtend.moveAtPercent(-0.2);
        leftExtend.moveAtPercent(-0.2);
    }

    public void tiltForward() {

        //   rightTilt.moveAtPercent(0.2);
        leftTilt.moveAtPercent(0.05);
    }

    public void tiltBack() {

        //  rightTilt.moveAtPercent(-0.2);
        leftTilt.moveAtPercent(-0.05);
    }

    public void stopTilt() {

        //   rightTilt.moveAtPercent(0.0);
        //  leftTilt.moveAtPercent(-0.0);
    }

    public void stopExtend() {

        leftExtend.moveAtPercent(0.0);
        //   rightExtend.moveAtPercent(-0.0);
        leftTilt.moveAtPercent(0.0);
        //  rightTilt.moveAtPercent(0.0);
    }


}
