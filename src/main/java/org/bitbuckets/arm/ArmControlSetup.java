package org.bitbuckets.arm;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IEncoder;
import org.bitbuckets.lib.hardware.IMotorController;

public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final IMotorController lowerJoint;
    final IMotorController upperJoint;

    public ArmControlSetup(ISetup<IMotorController> lowerJoint, ISetup<IMotorController> upperJoint) {
        this.lowerJoint = lowerJoint.build(path.addChild("lower-joint"));
        this.upperJoint = upperJoint.build(path.addChild("lower-joint"));
    }

    @Override
    public ArmControl build(ProcessPath path) {

        /*
        lowerJoint.getPIDController().setP(ArmConstants.kP);
        lowerJoint.getPIDController().setI(ArmConstants.kI);
        lowerJoint.getPIDController().setD(ArmConstants.kD);

        upperJoint.getPIDController().setP(ArmConstants.kP);
        upperJoint.getPIDController().setI(ArmConstants.kI);
        upperJoint.getPIDController().setD(ArmConstants.kD);

         */

        IEncoder lowerEncoder = new IEncoder() {
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
            public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
                return null;
            }
        };
        IEncoder upperEncoder = new IEncoder() {
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
            public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
                return null;
            }
        };

        /*
        lowerEncoder.setPositionConversionFactor(ArmConstants.lowerArmConversionFactor);
        upperEncoder.setPositionConversionFactor(ArmConstants.upperArmConversionFactor);
         */

        return new ArmControl(lowerJoint, upperJoint, lowerEncoder, upperEncoder);
    }
}

