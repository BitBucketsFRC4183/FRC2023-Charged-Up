package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

//TODO this needs to be run at 500 hz
public class DCSimController implements IMotorController, Runnable{


    final MotorConfig config;
    final DCMotorSim simulatedMotor;
    final PIDController simulatedPIDController;

    public DCSimController(MotorConfig config, DCMotorSim simulatedMotor, PIDController simulatedPIDController) {
        this.config = config;
        this.simulatedMotor = simulatedMotor;
        this.simulatedPIDController = simulatedPIDController;
    }


    @Override
    public double getMechanismFactor() {
        return config.encoderToMechanismCoefficient;
    }

    @Override
    public double getRotationsToMetersFactor() {
        return config.rotationToMeterCoefficient;
    }

    @Override
    public double getRawToRotationsFactor() {
        return 1; //It's a spark!
    }

    @Override
    public double getTimeFactor() {
        return config.timeCoefficient;
    }

    @Override
    public double getPositionRaw() {

        return simulatedMotor.getAngularPositionRotations() ;
    }

    @Override
    public double getVelocityRaw() {
        return simulatedMotor.getAngularVelocityRadPerSec() / Math.PI / 2.0; //rotations / second
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        throw new IllegalStateException("Cannot use force offset on simulated motor yet :( sorry!");
    }

    @Override
    public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {

    }

    @Override
    public void moveAtVoltage(double voltage) {
        simulatedMotor.setInputVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {
        simulatedMotor.setInputVoltage(percent * 12.0); //voltage time\
    }

    double lastSetpoint = 0.0;

    @Override
    public void moveToPosition(double position_encoderRotations) {
        //position raw should be encoder rotations
        double controllerOutput = simulatedPIDController.calculate(getPositionRaw(), position_encoderRotations);
        lastSetpoint = position_encoderRotations;

        simulatedMotor.setInputVoltage(controllerOutput);
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveAtVelocity(double velocity_encoderMetersPerSecond) {
        throw new IllegalStateException("velocity pid not simulated yet :( sorry");
    }

    @Override
    public double getSetpoint_mechanismRotations() {
        return lastSetpoint;
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new IllegalStateException("it's a sim motor you buffoon");
    }

    @Override
    public void run() {
        simulatedMotor.update(0.02); //TODO this needs to be accurate
    }


}
