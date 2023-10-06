package org.bitbuckets.arm.commands;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.arm.ArmControl;

public class ArmMoveToPositionCommand extends CommandBase {

    final float errorTolerance_rotations;
    final Vector<N2> desiredPosition_rotations;
    final ArmControl armControl;

    public ArmMoveToPositionCommand(ArmControl armControl, float errorTolerance_rotations, Vector<N2> desiredPosition_rotations) {
        this.errorTolerance_rotations = errorTolerance_rotations;
        this.desiredPosition_rotations = desiredPosition_rotations;
        this.armControl = armControl;

        addRequirements(armControl);
    }

    @Override
    public void execute() {
        armControl.commandArmToState(
                desiredPosition_rotations.get(0,0),
                desiredPosition_rotations.get(1,0)
        );
    }

    @Override
    public void end(boolean interrupted) {
        armControl.commandArmToVoltage(0,0);
    }

    @Override
    public boolean isFinished() {
        Vector<N2> error = armControl.getErrorQuantity_wrappedRotations();

        return error.get(0,0) <= errorTolerance_rotations &&
                error.get(1,0) <= errorTolerance_rotations;
    }
}
