package org.bitbuckets.drive.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.drive.IDriveControl;

public class StopDriveCommand extends CommandBase {

    final IDriveControl driveControl;

    public StopDriveCommand(IDriveControl driveControl) {
        this.driveControl = driveControl;

        addRequirements(driveControl);
    }

    @Override
    public void initialize() {
        driveControl.stop();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
