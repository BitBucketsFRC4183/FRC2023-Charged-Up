package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import static org.bitbuckets.lib.util.MathUtil.random;

/**
 * This runnable hooks into an existing TalonMotorController and emulates motor simulation
 * using basic math. not a replacement for using a seperate FlywheelMotorSetup, but useful when
 * you don't know your moment of inertia
 *
 * i didnt write half of this and the code quality tells.
 */
class SimulateMotorController implements Runnable {

    final TalonFX internalController;
    final boolean isSensorPhased;

    boolean _running = false;
    long _lastTime = System.nanoTime();
    double _vel = 0;

    public SimulateMotorController(TalonFX internalController, boolean isSensorPhased) {
        this.internalController = internalController;
        this.isSensorPhased = isSensorPhased;
    }

    double getPeriod() {
        // set the start time if not yet running
        if (!_running) {
            _lastTime = System.nanoTime();
            _running = true;
        }

        long now = System.nanoTime();
        final double period = (now - _lastTime) / 1000000.;
        _lastTime = now;

        return period;
    }


    static final double FULL_VELOCITY = 5100;
    static final double ACCELERATION = 0.75;

    @Override
    public void run() {

        final double period = getPeriod();
        final double accelAmount = FULL_VELOCITY / ACCELERATION * period / 1000;

        /// DEVICE SPEED SIMULATION

        double outPerc = internalController.getSimCollection().getMotorOutputLeadVoltage() / 12;

        if (isSensorPhased) { //TODO should we do this, or will the contorller do it for us?
            outPerc *= -1;
        }
        // Calculate theoretical velocity with some randomness
        double theoreticalVel = outPerc * FULL_VELOCITY * random(0.95, 1);
        // Simulate motor load
        if (theoreticalVel > _vel + accelAmount) {
            _vel += accelAmount;
        }
        else if (theoreticalVel < _vel - accelAmount) {
            _vel -= accelAmount;
        }
        else {
            _vel += 0.9 * (theoreticalVel - _vel);
        }
        // _pos += _vel * period / 100;

        /// SET SIM PHYSICS INPUTS
        internalController.getSimCollection().addIntegratedSensorPosition((int)(_vel * period / 100));
        internalController.getSimCollection().setIntegratedSensorVelocity((int)_vel);


        double supplyCurrent = Math.abs(outPerc) * 30 * random(0.95, 1.05);
        double statorCurrent = outPerc == 0 ? 0 : supplyCurrent / Math.abs(outPerc);
        internalController.getSimCollection().setSupplyCurrent(supplyCurrent);
        internalController.getSimCollection().setStatorCurrent(statorCurrent);

        internalController.getSimCollection().setBusVoltage(12 - outPerc * outPerc * 3/4 * random(0.95, 1.05));
    }
}
