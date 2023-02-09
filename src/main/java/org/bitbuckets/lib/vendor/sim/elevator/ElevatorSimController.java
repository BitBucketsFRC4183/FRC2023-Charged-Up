package org.bitbuckets.lib.vendor.sim.elevator;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

public class ElevatorSimController implements IMotorController, Runnable {

    final ElevatorSim elevatorSim;
    final PIDController positionPid;
    final MotorConfig motorConfig;

    ElevatorSimController(ElevatorSim elevatorSim, PIDController positionPid, MotorConfig motorConfig) {
        this.elevatorSim = elevatorSim;
        this.positionPid = positionPid;
        this.motorConfig = motorConfig;
    }

    double lastSetpoint = 0;

    @Override
    public double getMechanismFactor() {
        return motorConfig.mechanismCoefficient;
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
        return 1;
    }

    @Override
    public double getPositionRaw() {
        //meter to rotation coefficient
        return elevatorSim.getPositionMeters() / motorConfig.rotationToMeterCoefficient;
    }

    @Override
    public double getVelocityRaw() {
        return elevatorSim.getVelocityMetersPerSecond() / motorConfig.rotationToMeterCoefficient; //rotations per second
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        throw new IllegalStateException("unable to force offset of sim controller");
    }

    @Override
    public void moveAtVoltage(double voltage) {
        elevatorSim.setInputVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        elevatorSim.setInputVoltage(percent * 12.0);
    }

    @Override
    public void moveToPosition(double position_encoderRotations) {
        double output = positionPid.calculate(getPositionRaw(), position_encoderRotations);
        lastSetpoint = output;
        elevatorSim.setInputVoltage(output);
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        throw new IllegalStateException("cannot move at velocity :(");
    }

    @Override
    public double getSetpoint_rawUnits() {
        return lastSetpoint;
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot do anything");
    }

    @Override

    public void run() {
        elevatorSim.update(0.02);
        System.out.println(elevatorSim.getCurrentDrawAmps());
    }
}
