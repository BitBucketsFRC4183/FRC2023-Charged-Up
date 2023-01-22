package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.drive.controlsds.DriveControlSDSSetup;
import org.bitbuckets.drive.controlsds.NeoControlSDSSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.PIDIndex;

public class RobotSetup implements ISetup<RobotContainer> {


    @Override
    public RobotContainer build(ProcessPath path) {

        double[] predefPid = PIDIndex.CONSTANTS(1, 0, 0.1, 0, 0);
//
//        ModuleSetup frontLeft = new ModuleSetup(
//                new TalonSetup(
//                        1,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new TalonSetup(
//                        2,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        ModuleSetup frontRight = new ModuleSetup(
//                new TalonSetup(
//                        7,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new TalonSetup(
//                        8,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        ModuleSetup backLeftModule = new ModuleSetup(
//                new TalonSetup(
//                        5,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new TalonSetup(
//                        6,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        ModuleSetup backRightModule = new ModuleSetup(
//                new TalonSetup(
//                        3,
//                        true,
//                        DriveConstants.DRIVE_REDUCTION,
//                        DriveConstants.DRIVE_METERS_FACTOR,
//                        80,
//                        PIDIndex.EMPTY
//                ),
//                new TalonSetup(
//                        4,
//                        true,
//                        DriveConstants.TURN_REDUCTION,
//                        DriveConstants.TURN_METERS_FACTOR,
//                        20,
//                        predefPid
//                )
//        );
//
//        DriveControl driveControl = new DriveControlSetup(
//                frontLeft,
//                frontRight,
//                backLeftModule,
//                backRightModule
//        ).build(path.addChild("drive-control"));

//        DriveControlSDS driveControl = new Falcon500DriveControlSDSSetup().build(path.addChild("drive-control"));
        DriveControlSDS driveControl = new NeoControlSDSSetup().build(path.addChild("drive-control"));

        DriveInput input = new DriveInput(new Joystick(0));
//        DriveSubsystem driveSubsystem = new DriveSubsystem(input, driveControl);
        DriveSDSSubsystem driveSubsystem = new DriveSDSSubsystem(input, driveControl);

        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem);
    }
}
