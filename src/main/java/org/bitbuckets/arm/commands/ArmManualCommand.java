package org.bitbuckets.arm.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.arm.ArmControl;

public class ArmManualCommand extends CommandBase {

    final OperatorInput input;
    final ArmControl armControl;

    public ArmManualCommand(OperatorInput input, ArmControl armControl) {
        this.input = input;
        this.armControl = armControl;
    }

    @Override
    public void execute() {
        armControl.commandArmToPercent(
                input.getLowerArm_PercentOutput() * 0.35,
                input.getUpperArm_PercentOutput()
        );
    }

    @Override
    public boolean isFinished() {
        //once you stop touching it..

        return
                Math.abs(input.getLowerArm_PercentOutput()) < ArmControl.COMPONENT.manualModeThresholdToGoToManual() ||
                        Math.abs(input.getUpperArm_PercentOutput()) < ArmControl.COMPONENT.manualModeThresholdToGoToManual();
    }
}
