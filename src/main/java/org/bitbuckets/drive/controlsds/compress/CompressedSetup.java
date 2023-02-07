package org.bitbuckets.drive.controlsds.compress;

import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.ProcessPath;

public class CompressedSetup implements ISetup<ISwerveModule> {

    final ISetup<IMotorController> driveSetup;
    final ISetup<IMotorController> turnSetup;
    final ISetup<IAbsoluteEncoder> encoder;
    final double encoderCoefficient;

    public CompressedSetup(ISetup<IMotorController> driveSetup, ISetup<IMotorController> turnSetup, ISetup<IAbsoluteEncoder> encoder, double encoderCoefficient) {
        this.driveSetup = driveSetup;
        this.turnSetup = turnSetup;
        this.encoder = encoder;
        this.encoderCoefficient = encoderCoefficient;
    }

    @Override
    public ISwerveModule build(ProcessPath path) {
        CompressedModule module = new CompressedModule(
                driveSetup.build(path.addChild("drive")),
                turnSetup.build(path.addChild("turn")),
                encoder.build(path.addChild("absolute")),
                encoderCoefficient
        );

        path.registerLoop(module, "logging-loop");

        return module;
    }
}
