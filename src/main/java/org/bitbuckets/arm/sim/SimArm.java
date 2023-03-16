package org.bitbuckets.arm.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class SimArm implements IMotorController, HasLoop {

    final IDebuggable debuggable;
    final MechanismLigament2d ligament2d;
    final MotorConfig motorConfig;
    final SingleJointedArmSim sim;
    final PIDController armPositionPid;

    double setpoint_mechanismPos = 0;

    public SimArm(IDebuggable debuggable, MechanismLigament2d ligament2d, MotorConfig motorConfig, SingleJointedArmSim sim, PIDController armPositionPid) {
        this.debuggable = debuggable;
        this.ligament2d = ligament2d;
        this.motorConfig = motorConfig;
        this.sim = sim;
        this.armPositionPid = armPositionPid;
        //armPositionPid.enableContinuousInput(-1.0, 1.0);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        double currentPosition_mechanismRotations = sim.getAngleRads() / Math.PI / 2.0;

        cachedInputVoltage = armPositionPid.calculate(currentPosition_mechanismRotations, position_mechanismRotations);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        throw new UnsupportedOperationException("does not support");
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        return setpoint_mechanismPos;
    }

    @Override
    public double getVoltage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getCurrent() {
        return sim.getCurrentDrawAmps();
    }

    @Override
    public void goLimp() {

    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("bad");
    }

    double cachedInputVoltage = 0;

    public void loop() {
        sim.setInputVoltage(cachedInputVoltage);
        sim.update(0.02);

        double deg = Units.radiansToDegrees(sim.getAngleRads());

        double current = sim.getAngleRads() / Math.PI / 2.0;
        double setpoint = armPositionPid.getSetpoint();
        double error = armPositionPid.getPositionError();

        debuggable.log("error", error);
        debuggable.log("setpoint", setpoint);
        debuggable.log("current", current);

        sim.getCurrentDrawAmps();

        ligament2d.setAngle( deg  );
    }
}
