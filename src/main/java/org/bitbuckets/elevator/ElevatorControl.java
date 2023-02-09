package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;

public class ElevatorControl {

    private IMotorController leftExtend;
    private IMotorController rightExtend;
    private IMotorController leftTilt;
    private IMotorController rightTilt;

    final Debuggable debug;


    private MechanismLigament2d elevator;
    private MechanismLigament2d elevatorWrist;

    private double elevatorExtendSetpoint = 0;
    private double elevatorTiltSetpoint = 0;


    private static final double elevatorMinLength = 2;
    final double elevatorAngle = 90;

    double endEffectorMagnitude = 1.0;

    double x1 = 0;
    double theta1 = 0;

    ElevatorConstants elevatorConstants = new ElevatorConstants();


    public ElevatorControl(IMotorController leftExtend, IMotorController leftTilt, Debuggable debug, MechanismLigament2d elevator, MechanismLigament2d elevatorWrist) {
        this.leftExtend = leftExtend;
        this.debug = debug;
        this.rightExtend = rightExtend;
        this.leftTilt = leftTilt;
        this.rightTilt = rightTilt;
        this.elevatorWrist = elevatorWrist;
        this.elevator = elevator;


    }

    public void log() {
        debug.log("Left-extension-encoder", leftExtend.getPositionRaw());
        debug.log("right-extension-encoder", rightExtend.getPositionRaw());
        debug.log("Left-tilt-encoder", leftTilt.getPositionRaw());
        debug.log("right-tilt-encoder", rightTilt.getPositionRaw());
        debug.log("elevator-setpoint", elevatorExtendSetpoint);
        debug.log("elevator-setpoint", elevatorTiltSetpoint);


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

    public double unitToRotExtend(double unit) {
        return unit / ElevatorConstants.rotToMeterExtend * ElevatorConstants.getGearRatioExtend;

    }

    public void goToPosition(double extension, double tilt) {
        elevatorExtendSetpoint = extension;
        elevatorTiltSetpoint = tilt;
        leftTilt.moveToPosition(tilt);
        leftExtend.moveToPosition(extension);

    }

    public void gotoPositionButton() {
        goToPosition(60.0, 1.3);


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
