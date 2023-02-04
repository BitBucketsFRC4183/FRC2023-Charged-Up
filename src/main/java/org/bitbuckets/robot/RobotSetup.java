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

        return new RobotContainer(driveSubsystem, armSubsystem, visionControl, elevatorSubsystem, autoControl);
    }

    static OdometryControl buildOdometryControl(ProcessPath path, OdometryControlSetup odometryControlSetup) {
        if (!odometryEnabled) {
            return MockingUtil.buddy(OdometryControl.class);
        }
        return odometryControlSetup.build(path.addChild("odometry-control"));
    }
}
