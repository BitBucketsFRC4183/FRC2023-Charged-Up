package org.bitbuckets.arm;

import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;

public class ArmControlSetup implements ISetup<ArmControl> {

    final ArmDynamics feedFordward;
    final ISetup<IMotorController> lowMotor;
    final ISetup<IMotorController> upMotor;
    final ISetup<IPIDCalculator> lowCalculator;
    final ISetup<IPIDCalculator> highCalculator;
    final ISetup<IMotorController> gripperWheelMotor;
    final ISetup<IMotorController> gripperClawMotor;

    final ISetup<IAbsoluteEncoder> clawAbsEncoder;



    public ArmControlSetup(ArmDynamics feedFordward, ISetup<IMotorController> lowMotor, ISetup<IMotorController> upMotor, ISetup<IPIDCalculator> lowCalculator, ISetup<IPIDCalculator> highCalculator, ISetup<IMotorController> gripperWheelMotor, ISetup<IMotorController> gripperClawMotor, ISetup<IAbsoluteEncoder> clawAbsEncoder) {
        this.feedFordward = feedFordward;
        this.lowMotor = lowMotor;
        this.upMotor = upMotor;
        this.lowCalculator = lowCalculator;
        this.highCalculator = highCalculator;
        this.gripperWheelMotor = gripperWheelMotor;
        this.gripperClawMotor = gripperClawMotor;
        this.clawAbsEncoder = clawAbsEncoder;
    }

    @Override
    public ArmControl build(IProcess self) {
        var control = new ArmControl(
                feedFordward,
                self.childSetup("lower-joint", lowMotor),
                self.childSetup("upper-joint", upMotor),
                self.childSetup("lower-pid", lowCalculator),
                self.childSetup("upper-pid", highCalculator),
                self.childSetup("gripper-wheel-motor", gripperWheelMotor),
                self.childSetup("gripper-claw-motor", gripperClawMotor),
                self.childSetup("claw-abs-encoder",clawAbsEncoder),
                self.getDebuggable()
        );
        control.zeroArmAbs();
        control.zeroClawAbs();
        return control;
    }
}
