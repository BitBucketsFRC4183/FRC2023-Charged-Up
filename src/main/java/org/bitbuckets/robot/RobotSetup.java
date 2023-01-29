package org.bitbuckets.robot;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.arm.ArmInput;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.auto.AutoPath;
import org.bitbuckets.drive.DriveInput;
import org.bitbuckets.drive.DriveSDSSubsystem;
import org.bitbuckets.drive.balance.AutoAxisControl;
import org.bitbuckets.drive.balance.AutoAxisSetup;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.elevator.ElevatorControl;
import org.bitbuckets.elevator.ElevatorControlSetup;
import org.bitbuckets.elevator.ElevatorInput;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.gyro.GyroControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

import java.util.Optional;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {

        //TODO use neo controller here
     //   DriveControlSDS driveControl = MockingUtil.buddy(DriveControlSDS.class);
        ElevatorControlSetup elevatorControlSetup = new ElevatorControlSetup(
                new SparkSetup(1,new MotorConfig(1,0,0,false,false,1,false,false, Optional.empty())),
                new SparkSetup(2,new MotorConfig(1,0,0,false,false,1,false,false,Optional.empty())),
                new SparkSetup(3,new MotorConfig(1,0,0,false,false,1,false,false,Optional.empty())),
                new SparkSetup(4,new MotorConfig(1,0,0,false,false,1,false,false,Optional.empty()))

        );



        DriveInput input = new DriveInput(new Joystick(0));
        ElevatorInput elevatorInput = new ElevatorInput(new Joystick(1));
        ElevatorControl elevatorControl = elevatorControlSetup.build(path.addChild("elevator-control"));
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorControl,elevatorInput);

    

        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateValueTuner("path", AutoPath.TEST_PATH);

//        DriveControlSDS driveControl = new DriveControlSDSSetup().build(path.addChild("drive-control"));
//        DriveInput input = new DriveInput(new Joystick(0));
//        DriveSDSSubsystem driveSubsystem = new DriveSDSSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);

        ArmInput armInput = new ArmInput(
                new Joystick(1)
        );
        //labels: high priority
        //TODO use neos here
        ArmControlSetup armControlSetup = new ArmControlSetup(
                MockingUtil.noops(IMotorController.class),
                MockingUtil.noops(IMotorController.class)
        );

        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));

        armInput = new ArmInput(new Joystick(1));

        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);

        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(MockingUtil.buddy(DriveSDSSubsystem.class),armSubsystem,elevatorSubsystem);
    }
}
