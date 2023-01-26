package org.bitbuckets.drive.module;

import org.bitbuckets.drive.fenc.FilteredEncoder;
import org.bitbuckets.drive.old.FilteredEncoder;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.DataLogger;

public class ModuleSetup implements ISetup<DriveModule> {

    final ISetup<IMotorController> driveMotor;
    final ISetup<IMotorController> turnMotor;

    public ModuleSetup(ISetup<IMotorController> driveMotor, ISetup<IMotorController> turnMotor) {
        this.driveMotor = driveMotor;
        this.turnMotor = turnMotor;
    }

    @Override
    public DriveModule build(ProcessPath path) {

        IMotorController drive = driveMotor.build(path.addChild("drive"));
        IMotorController turn = turnMotor.build(path.addChild("turn"));

        DataLogger<ModuleDataAutoGen> logger = path.generatePushDataLogger(ModuleDataAutoGen::new);

        return new DriveModule(drive, turn, drive, new FilteredEncoder(turn), logger);
    }
}
