package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

//TODO this needs to be run at 500 hz
public class DCSimController implements IMotorController, HasLoop, HasLogLoop {


    final MotorConfig config;
    final DCMotorSim simulatedMotor;
    final PIDController simulatedPIDController;
    final IDebuggable debuggable;

    public DCSimController(MotorConfig config, DCMotorSim simulatedMotor, PIDController simulatedPIDController, IDebuggable debuggable) {
        this.config = config;
        this.simulatedMotor = simulatedMotor;
        this.simulatedPIDController = simulatedPIDController;
        this.debuggable = debuggable;
    }


    @Override
    public double getMechanismFactor() {
        return 1;
    } //shhh this needs to be fixed

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

    double position = 0;

    @Override
    public double getPositionRaw() {
        return simulatedMotor.getAngularPositionRad() / 2.0 / Math.PI;
        //return position ;
    }

    @Override
    public double getVelocityRaw() {
        return simulatedMotor.getAngularVelocityRadPerSec() / Math.PI / 2.0; //rotations / second
    }

    @Override
    public void forceOffset(double offsetUnits_baseUnits) {
        //do nothing for testing
    }

    @Override
    public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {

    }

    @Override
    public void moveAtVoltage(double voltage) {

        //debuggable.out("moveAtVoltage called with" + voltage);
        lastVoltage = voltage;

        simulatedMotor.setInputVoltage(voltage);
    }

    @Override
    public void moveAtPercent(double percent) {

        throw new UnsupportedOperationException();
        //simulatedMotor.setInputVoltage(percent * 12.0); //voltage time\
    }

    double lastSetpoint = 0.0;
    double lastVoltage = 0.0;

    @Override
    public void moveToPosition(double position_encoderRotations) {

        //position raw should be encoder rotations
        double controllerOutput = simulatedPIDController.calculate(getPositionRaw(), position_encoderRotations);
        lastSetpoint = position_encoderRotations;
        lastVoltage = controllerOutput;

        simulatedMotor.setInputVoltage(controllerOutput);
    }

    @Override
    public void moveToPosition_mechanismRotations(double position_mechanismRotations) {
        simulatedMotor.setInputVoltage(simulatedPIDController.calculate(getMechanismPositionAccum_rot(), position_mechanismRotations));
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
    public double getVoltage() {
        return lastVoltage;
    }

    @Override
    public double getCurrent() {
        return simulatedMotor.getCurrentDrawAmps();
    }

    @Override
    public void goLimp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        throw new IllegalStateException("Cannot get rawAccess to a sim motor. If you are seeing this, you probably need to add functionality to the IMotorController interface to support your rawAccess needs.");
    }

    @Override
    public void loop() {
        simulatedMotor.update(0.02); //TODO this needs to be accurate

        position = position + simulatedMotor.getAngularVelocityRadPerSec() * 0.02 / 2.0 / Math.PI;
    }


    @Override
    public void logLoop() {
        debuggable.log("position-mechanism", getPositionMechanism_meters());
        debuggable.log("velocity-mechanism", getVelocityMechanism_metersPerSecond());
        debuggable.log("velocity-encoder", getVelocityEncoder_metersPerSecond());
        debuggable.log("velocity-raw", getVelocityRaw());

        debuggable.log("rot-to-meter-coef", config.rotationToMeterCoefficient);
        debuggable.log("enc-to-mech", config.encoderToMechanismCoefficient);
        debuggable.log("rpm", simulatedMotor.getAngularVelocityRPM());
    }
}
