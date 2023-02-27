package org.bitbuckets.drive;

import config.Drive;
import config.DriveTurdSpecific;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.BalanceControl;
import org.bitbuckets.drive.balance.BalanceSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.DriveControllerSetup;
import org.bitbuckets.drive.controlsds.sds.ISwerveModule;
import org.bitbuckets.drive.controlsds.sds.SteerControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleSetup;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteEncoderSetup;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSteerMotorSetup;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;
import org.bitbuckets.odometry.IOdometryControl;
import org.bitbuckets.odometry.NavXOdometryControlSetup;
import org.bitbuckets.odometry.PidgeonOdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {

    public enum Mode {
        Neo,
        Talon,
        Sim
    }

    final boolean driveEnabled;
    final Mode mode;

    final OperatorInput operatorInput;
    final AutoSubsystem autoSubsystem;
    final IVisionControl visionControl;



    public DriveSubsystemSetup(boolean driveEnabled, Mode mode, OperatorInput input, AutoSubsystem autoSubsystem, IVisionControl visionControl) {
        this.driveEnabled = driveEnabled;
        this.mode = mode;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
        this.operatorInput = input;
    }

    @Override
    public DriveSubsystem build(IProcess self) {
        if (!driveEnabled) {
            return MockingUtil.buddy(DriveSubsystem.class);
        }

        BalanceControl balanceControl = self.childSetup("closed-loop", new BalanceSetup());

        DriveControl driveControl;
        IOdometryControl odometryControl;

        autoSubsystem.setDriveControl(driveControl);
        autoSubsystem.setOdometryControl(odometryControl);

        HoloControl holoControl = self.childSetup("holo-control", new HoloControlSetup(driveControl, visionControl, odometryControl));

        IValueTuner<DriveSubsystem.OrientationChooser> orientationTuner = self
                .generateTuner(ITuneAs.ENUM(DriveSubsystem.OrientationChooser.class), "set-orientation", DriveSubsystem.OrientationChooser.FIELD_ORIENTED);

        return new DriveSubsystem(
                operatorInput,
                MockingUtil.buddy(IOdometryControl.class),
                balanceControl,
                driveControl,
                autoSubsystem,
                MockingUtil.buddy(HoloControl.class),
                visionControl,
                orientationTuner,
                self.getDebuggable()
        );

    }


}
