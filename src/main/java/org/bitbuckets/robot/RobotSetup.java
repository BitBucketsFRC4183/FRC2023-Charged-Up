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
import org.bitbuckets.drive.controlsds.neo.NeoControlSDSSetup;
import org.bitbuckets.gyro.GyroControl;
import org.bitbuckets.gyro.GyroControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkRelativeMotorController;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {

        //TODO use neo controller here
        DriveControlSDS driveControl = MockingUtil.buddy(DriveControlSDS.class);

        DriveInput input = new DriveInput(new Joystick(0));
        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        IValueTuner<AutoPath> pathTuneable = path.generateValueTuner("path", AutoPath.TEST_PATH);


        //DriveSDSSubsystem driveSubsystem = new DriveSDSSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);


        //labels: high priority
        //TODO use neos here
        ArmControlSetup armControlSetup = new ArmControlSetup(
                MockingUtil.noops(IMotorController.class),
                MockingUtil.noops(IMotorController.class)
        );

        ArmControl armControl = armControlSetup.build(path.addChild("arm-control"));

        ArmInput armInput = new ArmInput(new Joystick(1));

        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl);

        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        return new RobotContainer(armSubsystem);
    }
}
