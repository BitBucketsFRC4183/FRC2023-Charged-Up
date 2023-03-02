package org.bitbuckets.lib.vendor.sim.dc;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;

/**
 * Generic simulator for a motor
 */
public class DCSimSetup implements ISetup<IMotorController> {

    final MotorConfig config;
    final SimInertiaConfig simInertiaConfig;
    final PIDConfig pidConfig;

    public DCSimSetup(MotorConfig config, SimInertiaConfig simInertiaConfig, PIDConfig pidConfig) {
        this.config = config;
        this.simInertiaConfig = simInertiaConfig;
        this.pidConfig = pidConfig;
    }

    @Override
    public IMotorController build(IProcess self) {
        if (true) {
            System.out.println("A");
            return new IMotorController() {
                @Override
                public double getMechanismFactor() {
                    return 0;
                }

                @Override
                public double getRotationsToMetersFactor() {
                    return 0;
                }

                @Override
                public double getRawToRotationsFactor() {
                    return 0;
                }

                @Override
                public double getTimeFactor() {
                    return 0;
                }

                @Override
                public double getPositionRaw() {
                    return 0;
                }

                @Override
                public double getVelocityRaw() {
                    return 0;
                }

                @Override
                public void forceOffset(double offsetUnits_baseUnits) {

                }

                @Override
                public void forceOffset_mechanismRotations(double offsetUnits_mechanismRotations) {

                }

                @Override
                public void moveAtVoltage(double voltage) {

                }

                @Override
                public void moveAtPercent(double percent) {

                }

                @Override
                public void moveToPosition(double position_encoderRotations) {

                }

                @Override
                public void moveToPosition_mechanismRotations(double position_mechanismRotations) {

                }

                @Override
                public void moveAtVelocity(double velocity_encoderMetersPerSecond) {

                }

                @Override
                public double getSetpoint_mechanismRotations() {
                    return 0;
                }

                @Override
                public double getVoltage() {
                    return 0;
                }

                @Override
                public double getCurrent() {
                    return 0;
                }

                @Override
                public void goLimp() {

                }

                @Override
                public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
                    return null;
                }
            };
        }


        DCMotorSim motorSim = new DCMotorSim(config.motorType, 1.0/ config.encoderToMechanismCoefficient, simInertiaConfig.momentOfInertia);
        PIDController pidController = new PIDController(pidConfig.kP, pidConfig.kI, pidConfig.kD);

        return new DCSimController(config, motorSim, pidController, self.getDebuggable());
    }
}
