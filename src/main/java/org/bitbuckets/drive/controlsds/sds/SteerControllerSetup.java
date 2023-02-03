package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;

public class SteerControllerSetup implements ISetup<ISteerController> {

    final ISetup<IMotorController> motor;
    final ISetup<IAbsoluteEncoder> encoder;

    final double sensorPositionCoefficient;

    public SteerControllerSetup(ISetup<IMotorController> motor, ISetup<IAbsoluteEncoder> encoder) {
        this.motor = motor;
        this.encoder = encoder;
        this.sensorPositionCoefficient = 1;
    }

    public SteerControllerSetup(ISetup<IMotorController> motor, ISetup<IAbsoluteEncoder> encoder, double sensorPositionCoefficient) {
        this.motor = motor;
        this.encoder = encoder;
        this.sensorPositionCoefficient = sensorPositionCoefficient;
    }

    @Override
    public ISteerController build(ProcessPath path) {
        var controller = new SteerController(
                motor.build(path.addChild("motor")),
                encoder.build(path.addChild("encoder")),
                sensorPositionCoefficient
        );

        // this used to be: checkNeoError(integratedEncoder.setPosition(absoluteEncoder.getAbsoluteAngle()), "Failed to set NEO encoder position");
        // in the motor setup
        controller.forceOffset(controller.getAbsoluteAngle() / sensorPositionCoefficient);
        return controller;
    }
}
