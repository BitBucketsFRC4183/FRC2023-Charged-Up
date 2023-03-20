package org.bitbuckets.drive;

import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.holo.WorseHoloControl;
import org.bitbuckets.drive.holo.WorseOdometryControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {


    final OperatorInput operatorInput;
    final AutoSubsystem autoSubsystem;
    final IVisionControl visionControl;
    final WorseOdometryControl odometryControl;
    final ISetup<BalanceControl> balanceControlSetup;
    final ISetup<WorseHoloControl> holoControlSetup;
    final IDriveControl driveControl;


    public DriveSubsystemSetup(OperatorInput input, AutoSubsystem autoSubsystem, IVisionControl visionControl, WorseOdometryControl odometryControl, ISetup<BalanceControl> balanceControlSetup, ISetup<WorseHoloControl> holoControlSetup, IDriveControl driveControl) {
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.operatorInput = input;
        this.odometryControl = odometryControl;
        this.balanceControlSetup = balanceControlSetup;
        this.holoControlSetup = holoControlSetup;
        this.driveControl = driveControl;
    }

    @Override
    public DriveSubsystem build(IProcess self) {
        return new DriveSubsystem(
                operatorInput,
                odometryControl,
                self.childSetup("balance-control", balanceControlSetup),
                driveControl,
                autoSubsystem,
                self.childSetup("holo-control", holoControlSetup),
                visionControl,
                self.generateTuner(ITuneAs.ENUM(DriveSubsystem.OrientationChooser.class), "set-orientation", DriveSubsystem.OrientationChooser.FIELD_ORIENTED),
                self.getDebuggable()
        );

    }


}
