package org.bitbuckets.robot;

import org.bitbuckets.RGB.RgbSubsystem;
import org.bitbuckets.RGB.RgbSubsystemSetup;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.arm.ArmSubsystemSetup;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.auto.AutoSubsystemSetup;
import org.bitbuckets.drive.DriveSubsystem;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.elevator.ElevatorSubsystem;
import org.bitbuckets.elevator.ElevatorSubsystemSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.macros.MacroSubsystem;
import org.bitbuckets.macros.MacroSubsystemSetup;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<RobotContainer> {

    final RobotStateControl robotStateControl;

    public RobotSetup(RobotStateControl robotStateControl) {
        this.robotStateControl = robotStateControl;
    }


    @Override
    public RobotContainer build(ProcessPath self) {

        RgbSubsystem rgbSubsystem = new RgbSubsystemSetup(false)
                .build(self.addChild("rgb-subsystem"));

        AutoSubsystem autoSubsystem = new AutoSubsystemSetup(true)
                .build(self.addChild("auto-subsystem"));

        IVisionControl visionControl = new VisionControlSetup(false)
                .build(self.addChild("vision-control"));

        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystemSetup(false, autoSubsystem)
                .build(self.addChild("elevator-subsystem"));

        ArmSubsystem armSubsystem = new ArmSubsystemSetup(false, autoSubsystem)
                .build(self.addChild("arm-subsystem"));

        DriveSubsystem driveSubsystem = new DriveSubsystemSetup(
                true,
                DriveSubsystemSetup.Mode.Talon,
                autoSubsystem,
                visionControl
        ).build(self.addChild("drive-subsystem"));

        MacroSubsystem macroSubsystem = new MacroSubsystemSetup(false)
                .build(self.addChild("macro-subsystem"));


        /**
         * Register the crasher runnable if
         */
        if (System.getenv().containsKey("CI")) {
            self.registerLoop(new SimulatorKiller(), "simulator-kill-loop");
        }


        return new RobotContainer(driveSubsystem, armSubsystem, elevatorSubsystem, autoSubsystem, macroSubsystem, rgbSubsystem);
    }


}
