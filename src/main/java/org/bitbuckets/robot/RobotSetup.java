package org.bitbuckets.robot;

import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.arm.ArmSubsystemSetup;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystemSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }


    @Override
    public RobotContainer build(ProcessPath self) {
        IVisionControl visionControl = new VisionControlSetup(false)
                .build( self.addChild("vision-control") );

        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystemSetup(false)
                .build( self.addChild("elevator-subsystem") );

        ArmSubsystem armSubsystem = new ArmSubsystemSetup(false)
                .build(self.addChild("arm-subsystem"));

        DriveSubsystem driveSubsystem = new DriveSubsystemSetup(
                true,
                robotStateControl,
                visionControl
        ).build(self.addChild("drive-subsystem"));

        /**
         * Register the crasher runnable if
         */
        if (System.getenv().containsKey("CI")) {
            self.registerLoop(new SimulatorKillAspect(), "simulator-kill-loop");
        }


        return new RobotContainer(driveSubsystem, armSubsystem, elevatorSubsystem);
    }


}
