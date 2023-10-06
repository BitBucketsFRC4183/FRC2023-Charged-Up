package org.bitbuckets.arm.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.bitbuckets.arm.ArmControl;

public class StopGripperCommand extends InstantCommand {

    public StopGripperCommand(ArmControl control) {
        super(control::stopGripper, control);
    }
}
