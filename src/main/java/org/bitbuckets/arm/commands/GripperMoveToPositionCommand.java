package org.bitbuckets.arm.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.arm.ArmControl;

public class GripperMoveToPositionCommand extends CommandBase {

    final ArmControl armControl;
    final double tolerance_rotations;
    final double gripperPosition_rotations;

    public GripperMoveToPositionCommand(ArmControl armControl, double tolerance_rotations, double gripperPosition_rotations) {
        this.armControl = armControl;
        this.tolerance_rotations = tolerance_rotations;
        this.gripperPosition_rotations = gripperPosition_rotations;
    }



    @Override
    public void execute() {
        armControl.commandGripperClawToPosition(gripperPosition_rotations);
    }

    @Override
    public boolean isFinished() {
        return armControl.gripperClawError_rotations() <= tolerance_rotations;
    }
}
