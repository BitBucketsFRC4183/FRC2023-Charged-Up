package org.bitbuckets.drive;

import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.balance.ExperimentalBalanceControl;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.vision.IVisionControl;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {


    final OperatorInput operatorInput;
    final AutoSubsystem autoSubsystem;
    final IVisionControl visionControl;
    final IOdometryControl odometryControl;
    final ISetup<BalanceControl> balanceControlSetup;
    final ISetup<HoloControl> holoControlSetup;
    final IDriveControl driveControl;

    final ISetup<IPIDCalculator> calculator;

    public DriveSubsystemSetup(OperatorInput operatorInput, AutoSubsystem autoSubsystem, IVisionControl visionControl, IOdometryControl odometryControl, ISetup<BalanceControl> balanceControlSetup, ISetup<HoloControl> holoControlSetup, IDriveControl driveControl, ISetup<IPIDCalculator> calculator) {
        this.operatorInput = operatorInput;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.odometryControl = odometryControl;
        this.balanceControlSetup = balanceControlSetup;
        this.holoControlSetup = holoControlSetup;
        this.driveControl = driveControl;
        this.calculator = calculator;
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
                self.getDebuggable(),
                self.childSetup("time-fb", calculator)
        );

    }


}
