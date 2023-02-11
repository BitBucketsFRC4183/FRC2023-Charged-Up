package org.bitbuckets.drive;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.drive.balance.ClosedLoopsControl;
import org.bitbuckets.drive.balance.ClosedLoopsSetup;
import org.bitbuckets.drive.controlsds.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSetup;
import org.bitbuckets.drive.controlsds.sds.DriveControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SteerControllerSetup;
import org.bitbuckets.drive.controlsds.sds.SwerveModuleSetup;
import org.bitbuckets.drive.holo.HoloControl;
import org.bitbuckets.drive.holo.HoloControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.spark.SparkDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSteerMotorSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;
import org.bitbuckets.odometry.OdometryControl;
import org.bitbuckets.odometry.OdometryControlSetup;
import org.bitbuckets.vision.IVisionControl;

public class DriveSubsystemSetup implements ISetup<DriveSubsystem> {

    final boolean driveEnabled;
    final boolean isSimulated;

    final AutoSubsystem autoSubsystem;
    final IVisionControl visionControl;

    public DriveSubsystemSetup(boolean driveEnabled, boolean isSimulated, AutoSubsystem autoSubsystem, IVisionControl visionControl) {
        this.driveEnabled = driveEnabled;
        this.isSimulated = isSimulated;
        this.autoSubsystem = autoSubsystem;
        this.visionControl = visionControl;
    }

    @Override
    public DriveSubsystem build(ProcessPath self) {
        if (!driveEnabled) {
            return MockingUtil.buddy(DriveSubsystem.class);
        }

        DriveInput input = new DriveInput(new Joystick(0));
        ClosedLoopsControl closedLoopsControl = new ClosedLoopsSetup()
                .build(self.addChild("axis-control"));

        DriveControl driveControl;
        if (isSimulated) {
            driveControl = buildSimDriveControl(self);
        } else {
            driveControl = buildNeoDriveControl(self); //or use talons, when they work}
        }

        OdometryControl odometryControl = new OdometryControlSetup(driveControl, visionControl, 5)
                .build(self.addChild("odo-control"));
        HoloControl holoControl = new HoloControlSetup(driveControl, visionControl, odometryControl)
                .build(self.addChild("holo-control"));

        IValueTuner<DriveSubsystem.OrientationChooser> orientationTuner = self
                .generateEnumTuner("set-orientation", DriveSubsystem.OrientationChooser.class, DriveSubsystem.OrientationChooser.FIELD_ORIENTED);
        Debuggable debuggable = self.generateDebugger();

        return new DriveSubsystem(
                input,
                odometryControl,
                closedLoopsControl,
                driveControl,
                autoSubsystem,
                holoControl,
                visionControl,
                orientationTuner,
                debuggable

        );

    }


    DriveControl buildNeoDriveControl(ProcessPath path) {
        // used to configure the spark motor in SparkSetup


        DriveControl driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.FRONT_LEFT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.FRONT_LEFT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_LEFT_ENCODER_CHANNEL, DriveConstants.FRONT_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.FRONT_RIGHT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.FRONT_RIGHT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL, DriveConstants.FRONT_RIGHT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.BACK_LEFT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.BACK_LEFT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.BACK_LEFT_ENCODER_CHANNEL, DriveConstants.BACK_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new SparkDriveMotorSetup(DriveConstants.BACK_RIGHT_DRIVE_ID, DriveConstants.DRIVE_CONFIG, DriveConstants.MK4I_L2)),
                        new SteerControllerSetup(
                                new SparkSteerMotorSetup(DriveConstants.BACK_RIGHT_STEER_ID, DriveConstants.STEER_CONFIG, DriveConstants.STEER_PID, DriveConstants.MK4I_L2),
                                new ThriftyEncoderSetup(DriveConstants.BACK_RIGHT_ENCODER_CHANNEL, DriveConstants.BACK_RIGHT_OFFSET))
                )
        ).build(path.addChild("drive-control"));

        return driveControl;
    }

    DriveControl buildSimDriveControl(ProcessPath path) {
        // used to configure the spark motor in SparkSetup


        DriveControl driveControl = new DriveControlSetup(
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_LEFT_ENCODER_CHANNEL, DriveConstants.FRONT_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.FRONT_RIGHT_ENCODER_CHANNEL, DriveConstants.FRONT_RIGHT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.BACK_LEFT_ENCODER_CHANNEL, DriveConstants.BACK_LEFT_OFFSET))
                ),
                new SwerveModuleSetup(
                        new DriveControllerSetup(new DCSimSetup(DriveConstants.DRIVE_CONFIG, DriveConstants.DRIVE_MOTOR, DriveConstants.DRIVE_PID)),
                        new SteerControllerSetup(
                                new DCSimSetup(DriveConstants.STEER_CONFIG, DriveConstants.STEER_MOTOR, DriveConstants.STEER_PID),
                                new ThriftyEncoderSetup(DriveConstants.BACK_RIGHT_ENCODER_CHANNEL, DriveConstants.BACK_RIGHT_OFFSET))
                )
        ).build(path.addChild("drive-control"));

        return driveControl;
    }

}
