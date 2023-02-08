package org.bitbuckets.drive.controlsds.compress;

import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;

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
    public ISwerveModule build(ProcessPath self) {
        CompressedModule module = new CompressedModule(
                driveSetup.build(self.addChild("drive")),
                turnSetup.build(self.addChild("turn")),
                encoder.build(self.addChild("absolute")),
                encoderCoefficient
        );

        self.registerLoop(module, "logging-loop");

        return module;
    }
}
