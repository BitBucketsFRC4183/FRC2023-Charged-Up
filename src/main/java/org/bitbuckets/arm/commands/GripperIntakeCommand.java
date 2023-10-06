package org.bitbuckets.arm.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.arm.ArmControl;

public class GripperIntakeCommand extends CommandBase {

    final ArmControl control;
    final OperatorInput input;


    public GripperIntakeCommand(ArmControl control, OperatorInput input) {
        this.control = control;
        this.input = input;

        addRequirements(control);
    }

    @Override
    public void execute() {
        if(input.isCube()) {
            control.intakeGripperCube();
        }
        else {
            control.intakeGripperCone();
        }
    }
}
