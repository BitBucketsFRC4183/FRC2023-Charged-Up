package org.bitbuckets.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.control.DriveControl;
import org.bitbuckets.drive.control.DriveControlSetup;
import org.bitbuckets.drive.module.DriveModule;
import org.bitbuckets.drive.module.DriveModuleDataAutoGen;
import org.bitbuckets.drive.module.DriveModuleSetup;
import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.vendor.ctre.TalonSetup;

public class RobotSetup implements ISetup<RobotContainer> {


    @Override
    public RobotContainer build(ProcessPath path) {

        double[] predefPid = PIDIndex.CONSTANTS(0.02,0,0.1,0,0);

        DriveModuleSetup frontLeft = new DriveModuleSetup(
                new TalonSetup(
                        1,
                        true,
                        DriveConstants.DRIVE_REDUCTION,
                        DriveConstants.DRIVE_METERS_FACTOR,
                        80,
                        PIDIndex.EMPTY
                ),
                new TalonSetup(
                        2,
                        true,
                        DriveConstants.TURN_REDUCTION,
                        DriveConstants.TURN_METERS_FACTOR,
                        20,
                        predefPid
                ),
                9,
                -232.55
        );

        DriveModuleSetup frontRight = new DriveModuleSetup(
                new TalonSetup(
                        7,
                        true,
                        DriveConstants.DRIVE_REDUCTION,
                        DriveConstants.DRIVE_METERS_FACTOR,
                        80,
                        PIDIndex.EMPTY
                ),
                new TalonSetup(
                        8,
                        true,
                        DriveConstants.TURN_REDUCTION,
                        DriveConstants.TURN_METERS_FACTOR,
                        20,
                        predefPid
                ),
                12,
                -(331.96 - 180.0)
        );

        DriveModuleSetup backLeftModule = new DriveModuleSetup(
                new TalonSetup(
                        5,
                        true,
                        DriveConstants.DRIVE_REDUCTION,
                        DriveConstants.DRIVE_METERS_FACTOR,
                        80,
                        PIDIndex.EMPTY
                ),
                new TalonSetup(
                        6,
                        true,
                        DriveConstants.TURN_REDUCTION,
                        DriveConstants.TURN_METERS_FACTOR,
                        20,
                        predefPid
                ),
                11,
                -255.49
        );

        DriveModuleSetup backRightModule = new DriveModuleSetup(
                new TalonSetup(
                        3,
                        true,
                        DriveConstants.DRIVE_REDUCTION,
                        DriveConstants.DRIVE_METERS_FACTOR,
                        80,
                        PIDIndex.EMPTY
                ),
                new TalonSetup(
                        4,
                        true,
                        DriveConstants.TURN_REDUCTION,
                        DriveConstants.TURN_METERS_FACTOR,
                        20,
                        predefPid
                ),
                10,
                -(70.66 + 180)
        );


        IMotorController empty = new IMotorController() {
            @Override
            public double getMechanismFactor() {
                return 0;
            }

            @Override
            public double getRotationsToMetersFactor() {
                return 0;
            }

            @Override
            public double getRawToRotationsFactor() {
                return 0;
            }

            @Override
            public double getTimeFactor() {
                return 0;
            }

            @Override
            public double getPositionRaw() {
                return 0;
            }

            @Override
            public double getVelocityRaw() {
                return 0;
            }

            @Override
            public void forceOffset(double offsetUnits_baseUnits) {

            }

            @Override
            public void moveAtPercent(double percent) {

            }

            @Override
            public void moveToPosition(double position_encoderRotations) {

            }

            @Override
            public void moveAtVelocity(double velocity_encoderMetersPerSecond) {

            }

            @Override
            public double getSetpoint_rawUnits() {
                return 0;
            }

            @Override
            public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
                return null;
            }
        };
        DriveModule mod = new DriveModule(empty, empty, empty, empty, new DataLogger<>(0,new LogDriver(new IdentityDriver()),new DriveModuleDataAutoGen()));

        DriveControl driveControl = new DriveControlSetup(
                (a) -> mod,
                (a) -> mod,
                (a) -> mod,
                backRightModule
        ).build(path.addChild("drive-control"));



        DriveInput input = new DriveInput(new Joystick(0));
        DriveSubsystem driveSubsystem = new DriveSubsystem(input, driveControl);

        //WPI_TalonFX fx = new WPI_TalonFX(4);


        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem);
    }
}
