package org.bitbuckets.drive.controlsds.compress;

import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.IProcess;

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
    public ISwerveModule build(IProcess self) {
        CompressedModule module = new CompressedModule(
                self.childSetup("drive-motor", driveSetup),
                self.childSetup("turn-motor", turnSetup),
                self.childSetup("abs-encoder", encoder),
                encoderCoefficient
        );

        self.registerLogLoop(module);

        return module;
    }
}
