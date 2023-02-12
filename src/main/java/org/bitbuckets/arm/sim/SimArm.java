package org.bitbuckets.arm.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.arm.ArmConstants;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.Debuggable;

public class SimArm implements IMotorController {

    final Debuggable debuggable;
    final MechanismLigament2d ligament2d;
    final MotorConfig motorConfig;
    final SingleJointedArmSim sim;
    final PIDController armPositionPid;

    double setpoint_encoderRot = 0;

    public SimArm(Debuggable debuggable, MechanismLigament2d ligament2d, MotorConfig motorConfig, SingleJointedArmSim sim, PIDController armPositionPid) {
        this.debuggable = debuggable;
        this.ligament2d = ligament2d;
        this.motorConfig = motorConfig;
        this.sim = sim;
        this.armPositionPid = armPositionPid;
    }

    @Override
    public double getMechanismFactor() {
        return motorConfig.encoderToMechanismCoefficient;
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
        return sim.getAngleRads() / Math.PI / 2.0;
    }

    @Override
    public double getVelocityRaw() {
        return sim.getVelocityRadPerSec() / Math.PI / 2.0;
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        throw new UnsupportedOperationException("69");
    }

    @Override
    public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {
        throw new UnsupportedOperationException("69");
    }

    @Override
    public void moveAtVoltage(double voltage) {
        cachedInputVoltage = voltage;
    }

    @Override
    public void moveAtPercent(double percent) {
        cachedInputVoltage = percent * 12.0;
    }

    @Override
    public void moveToPosition(double setpoint_encoderRotations) {
        debuggable.log("move-to-position", setpoint_encoderRotations);

        cachedInputVoltage = armPositionPid.calculate(getEncoderPositionAccumulated_rot(),setpoint_encoderRotations);
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        debuggable.log("move-to-mech", position_mechanismRotations);

        moveToPosition(position_mechanismRotations / ArmConstants.LOWER_ARM_GEAR_RATIO);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        throw new UnsupportedOperationException("does not support");
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        return setpoint_encoderRot * (motorConfig.encoderToMechanismCoefficient);
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("bad");
    }

    double cachedInputVoltage = 0;

    public void runSimulationLoop() {
        sim.setInputVoltage(cachedInputVoltage);
        sim.update(0.02);

        double deg = Units.radiansToDegrees(sim.getAngleRads());

        debuggable.log("voltage", cachedInputVoltage);
        debuggable.log("nextAngle", deg);
        debuggable.log("error", this.getError_mechanismRotations());

        sim.getCurrentDrawAmps();

        ligament2d.setAngle( deg  );
    }
}
