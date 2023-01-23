package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.arm.ArmInput;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.module.ModuleSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.vendor.ctre.TalonSetup;

public class RobotSetup implements ISetup<RobotContainer> {


    @Override
    public RobotContainer build(ProcessPath path) {

        double[] predefPid = PIDIndex.CONSTANTS(1, 0, 0.1, 0, 0);

        ModuleSetup frontLeft = new ModuleSetup(
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
                )
        );

        ModuleSetup frontRight = new ModuleSetup(
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
                )
        );

        ModuleSetup backLeftModule = new ModuleSetup(
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
                )
        );

        ModuleSetup backRightModule = new ModuleSetup(
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
                )
        );


        DriveInput driveInput = new DriveInput(new Joystick(0));
        // DriveSubsystem driveSubsystem = new DriveSubsystem(driveInput, driveControl);

        ArmControl armControl = new ArmControlSetup().build(path.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1));
        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);

        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(armSubsystem);
    }
}
