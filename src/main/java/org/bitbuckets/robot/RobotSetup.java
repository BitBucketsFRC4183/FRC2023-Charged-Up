package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.arm.ArmSubsystemSetup;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystemSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.vision.VisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }

    @Override
    public RobotContainer build(ProcessPath path) {

        VisionControl visionControl = new VisionControlSetup(true)
                .build(path.addChild("vision-control") );

        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystemSetup(false)
                .build(path.addChild("elevator-subsystem") );

        ArmSubsystem armSubsystem = new ArmSubsystemSetup(true)
                .build(path.addChild("arm-subsystem"));

        DriveSubsystem driveSubsystem = new DriveSubsystemSetup(
                false,
                robotStateControl,
                visionControl
        ).build(path.addChild("drive-subsystem"));


        return new RobotContainer(driveSubsystem, armSubsystem, elevatorSubsystem);
    }


}
