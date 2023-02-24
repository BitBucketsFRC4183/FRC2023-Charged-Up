package org.bitbuckets.arm.sim;

import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class SimArmControllerPhysics implements IMotorController {


    final boolean isUpper;
    final MotorConfig motorConfig;
    final ArmSimNew armSimNew;
    final MechanismLigament2d ligament2d;


    public SimArmControllerPhysics(boolean isUpper, MotorConfig motorConfig, ArmSimNew armSimNew, MechanismLigament2d ligament2d) {
        this.isUpper = isUpper;
        this.motorConfig = motorConfig;
        this.armSimNew = armSimNew;
        this.ligament2d = ligament2d;
    }

    @Override
    public double getMechanismFactor() {
        return 1;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return motorConfig.rotationToMeterCoefficient;
    }

    @Override
    public double getRawToRotationsFactor() {
        return 1;
    }

    @Override
    public double getTimeFactor() {
        return motorConfig.timeCoefficient;
    }

    @Override
    public double getPositionRaw() {

        if (isUpper) {
            return armSimNew.readLowerArmStates().get(1,0) / Math.PI / 2.0;
        } else {
            return armSimNew.readLowerArmStates().get(0,0) / Math.PI / 2.0;
        }

    }

    @Override
    public double getVelocityRaw() {
        if (isUpper) {
            return armSimNew.readLowerArmStates().get(3,0) / Math.PI / 2.0 * getTimeFactor();
        } else {
            return armSimNew.readLowerArmStates().get(2,0) / Math.PI / 2.0 * getTimeFactor();
        }

    }


    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveAtVoltage(double voltage) {
        armSimNew.setDesiredLowVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        armSimNew.setDesiredLowVoltage(percent *12.0);

    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getVoltage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getCurrent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void setTheAngleOfTheSimShit() {
        ligament2d.setAngle(getPositionRaw() * 360.0);
    }
}
