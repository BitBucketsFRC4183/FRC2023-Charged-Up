package org.bitbuckets.robot;

import org.bitbuckets.arm.*;
import org.bitbuckets.auto.AutoControl;
import org.bitbuckets.auto.AutoControlSetup;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.elevator.*;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.odometry.OdometryControl;
import org.bitbuckets.odometry.OdometryControlSetup;
import org.bitbuckets.vision.VisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<RobotContainer> {
    final static boolean odometryEnabled = false;

    final static boolean driveEnabled = false;
    final static boolean armEnabled = true;
    final static boolean elevatorEnabled = true;
    final static boolean odometryEnabled = false;

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {

        VisionControl visionControl = new VisionControlSetup().build(path.addChild("vision-control"));
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystemSetup().build(path.addChild("elevator-subsystem"));
        AutoControl autoControl = new AutoControlSetup().build(path.addChild("auto-control"));
        ArmSubsystem armSubsystem = new ArmSubsystemSetup().build(path.addChild("arm-subsystem"));
        DriveSubsystem driveSubsystem = new DriveSubsystemSetup(robotStateControl, autoControl).build(path.addChild("drive-subsystem"));

        DriveInput input = new DriveInput(new Joystick(0));

        GyroControl gyroControl = new GyroControlSetup(5).build(path.addChild("gyro-control"));
        AutoAxisControl autoAxisControl = new AutoAxisSetup().build(path.addChild("axis-control"));
        //also throwing errors since I'm no longer using TestPath, but rather the array
        IValueTuner<AutoPath> pathTuneable = path.generateValueTuner("path", AutoPath.AUTO_TEST_PATH_ONE);


        AutoControl autoControl = null;
        DriveSubsystem driveSubsystem = new DriveSubsystem(input, robotStateControl, gyroControl, autoAxisControl, driveControl, autoControl, pathTuneable);

        ArmControl armControl = buildArmControl(path);
        ArmInput armInput = new ArmInput(
                new Joystick(1)
        );

        DoubleJointedArmSim armSim = new DoubleJointedArmSim();

        ArmSubsystem armSubsystem = new ArmSubsystem(armInput, armControl, path.generateStringLogger("arm-subsystem"), armSim);

        autoControl = new AutoControlSetup(
                armControl
        ).build(path.addChild("AutoControlSetup"));


        //SYSTEMS_GREEN.setOn(); //LET'S WIN SOME DAMN REGIONALS!!

        // this is crashing
//        OdometryControlSetup odometryControlSetup = new OdometryControlSetup(driveControl, visionControl, gyroControl, new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
//        buildOdometryControl(path, odometryControlSetup);

        return new RobotContainer(driveSubsystem, armSubsystem, visionControl);
    }

        return new RobotContainer(driveSubsystem, armSubsystem, visionControl, elevatorSubsystem, autoControl);
    }

    static OdometryControl buildOdometryControl(ProcessPath path, OdometryControlSetup odometryControlSetup) {
        if (!odometryEnabled) {
            return MockingUtil.buddy(OdometryControl.class);
        }
        return odometryControlSetup.build(path.addChild("odometry-control"));
    }
}
