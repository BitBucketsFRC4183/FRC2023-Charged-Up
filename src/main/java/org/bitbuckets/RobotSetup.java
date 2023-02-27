package org.bitbuckets;

import config.Setups;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.arm.ArmSubsystem;
import org.bitbuckets.arm.ArmSubsystemSetup;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.auto.AutoSubsystemSetup;
import org.bitbuckets.drive.DriveSubsystemSetup;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SimulatorKiller;
import org.bitbuckets.vision.IVisionControl;
import org.bitbuckets.vision.VisionControlSetup;

public class RobotSetup implements ISetup<Void> {




    @Override
    public Void build(IProcess self) {

        OperatorInput operatorInput = new OperatorInput(
                new Joystick(0),
                new Joystick(1)
        );

        AutoSubsystem autoSubsystem = self.childSetup("auto-system", new AutoSubsystemSetup(false, Setups.AUTO_CONTROL));
        IVisionControl visionControl = self.childSetup("vision-system", new VisionControlSetup(false));
        ArmSubsystem armSubsystem = self.childSetup("arm-system", new ArmSubsystemSetup(autoSubsystem, operatorInput, armControlISetup, gripperControlISetup, false));

        DriveSubsystemSetup driveSubsystem = new DriveSubsystemSetup(
                true,
                DriveSubsystemSetup.Mode.Neo,
                operatorInput,
                autoSubsystem,
                visionControl
        );
        self.childSetup("drive-system", driveSubsystem);

        /**
         * Register the crasher runnable if we're in github
         */
        if (System.getenv().containsKey("CI")) {
            self.registerLogicLoop(new SimulatorKiller());
        }

        
        return null;
    }


}
