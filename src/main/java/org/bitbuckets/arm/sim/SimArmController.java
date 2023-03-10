package org.bitbuckets.arm.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class SimArmController implements IMotorController, HasLoop, HasLogLoop {

    final boolean isUpper;
    final MotorConfig motorConfig;
    final SimArmCore core;
    final MechanismLigament2d ligament2d;

    final PIDController controller;


    public SimArmController(boolean isUpper, MotorConfig motorConfig, SimArmCore core, MechanismLigament2d ligament2d, PIDController controller) {
        this.isUpper = isUpper;
        this.motorConfig = motorConfig;
        this.core = core;
        this.ligament2d = ligament2d;
        this.controller = controller;
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
            return core.readLowerArmStates().get(1,0) / Math.PI / 2.0;
        } else {
            return core.readLowerArmStates().get(0,0) / Math.PI / 2.0;
        }

    }

    @Override
    public double getVelocityRaw() {
        if (isUpper) {
            return core.readLowerArmStates().get(3,0) / Math.PI / 2.0 * getTimeFactor();
        } else {
            return core.readLowerArmStates().get(2,0) / Math.PI / 2.0 * getTimeFactor();
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
        core.setDesiredLowVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        core.setDesiredLowVoltage(percent * 12.0);
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
    public void goLimp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void logLoop() {
        //TODO log everything else

        ligament2d.setAngle(Units.rotationsToDegrees(core.getLowerArmPosition()));
    }

    @Override
    public void loop() {

    }
}
