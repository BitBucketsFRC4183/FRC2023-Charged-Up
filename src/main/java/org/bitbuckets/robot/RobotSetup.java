package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmInput;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.drive.balance.AutoAxisControl;
import org.bitbuckets.drive.balance.AutoAxisSetup;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.drive.controlsds.DriveControlSDSSetup;
import org.bitbuckets.elevator.ElevatorControl;
import org.bitbuckets.elevator.ElevatorControlSetup;
import org.bitbuckets.elevator.ElevatorInput;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.gyro.GyroControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.vendor.ctre.TalonSetup;
import org.bitbuckets.lib.vendor.ctre.TalonSetup;

import javax.xml.crypto.Data;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {
        double[] predefPid = PIDIndex.CONSTANTS(1, 0, 0.1, 0, 0);

        ElevatorControlSetup elevatorControlSetup = new ElevatorControlSetup(
                new TalonSetup(1,false,0,0,0,new double[]{0,0,0}),
                new TalonSetup(2,false,0,0,0,new double[]{0,0,0}),
                new TalonSetup(3,false,0,0,0,new double[]{0,0,0}),
                new TalonSetup(4,false,0,0,0,new double[]{0,0,0}));

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

        ElevatorInput elevatorInput = new ElevatorInput(new Joystick(1));
        ElevatorControl elevatorControl = elevatorControlSetup.build(path.addChild("elevator-control"));
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorControl,elevatorInput);


        ArmInput armInput = new ArmInput(new Joystick(0));
        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, null);

        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateTuneable("path", AutoPath.TEST_PATH);

        DriveControlSDS driveControl = new DriveControlSDSSetup().build(path.addChild("drive-control"));
        DriveInput input = new DriveInput(new Joystick(0));
        DriveSDSSubsystem driveSubsystem = new DriveSDSSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);
        
        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(driveSubsystem, armSubsystem,elevatorSubsystem);
    }
}
