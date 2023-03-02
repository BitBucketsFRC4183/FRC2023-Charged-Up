package org.bitbuckets.drive.controlsds.sds;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
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
    public ISteerController build(IProcess self) {


        var controller = new SteerController(
                self.childSetup("motor", motor),
                self.childSetup("encoder", encoder),
                sensorPositionCoefficient
        );


        // this used to be: checkNeoError(integratedEncoder.setPosition(absoluteEncoder.getAbsoluteAngle()), "Failed to set NEO encoder position");
        // in the motor setup
        controller.forceOffset(controller.getAbsoluteAngle() / sensorPositionCoefficient);
        return controller;
    }
}
