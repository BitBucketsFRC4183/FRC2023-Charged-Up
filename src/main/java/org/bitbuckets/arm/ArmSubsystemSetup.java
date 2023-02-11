package org.bitbuckets.arm;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import org.bitbuckets.arm.sim.ArmConfig;
import org.bitbuckets.arm.sim.SimArm;
import org.bitbuckets.arm.sim.SimArmSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final boolean isEnabled;

    public ArmSubsystemSetup(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public ArmSubsystem build(ProcessPath self) {
        if (!isEnabled) {
            return MockingUtil.buddy(ArmSubsystem.class);
        }

        ISetup<IMotorController> lowerArm;
        ISetup<IMotorController> upperArm;

        if (self.isReal()) {
            lowerArm = new SparkSetup(9, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID);
            upperArm = new SparkSetup(10, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID);
        } else {
            lowerArm = new SimArmSetup(ArmConstants.LOWER_CONFIG, new ArmConfig(ArmConstants.LOWER_JOINT_LENGTH, 1, 1, 1,true), ArmConstants.LOWER_PID);
            upperArm = new SimArmSetup(ArmConstants.UPPER_CONFIG, new ArmConfig(ArmConstants.UPPER_JOINT_LENGTH, 1, 1, 1,true), ArmConstants.UPPER_PID);
        }

        ArmControlSetup armControlSetup = new ArmControlSetup(
                new SparkSetup(11, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID),
                new SparkSetup(9, ArmConstants.LOWER_CONFIG1, ArmConstants.LOWER_PID),
                new SparkSetup(10, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID)
        );

        ArmControl armControl = armControlSetup.build(self.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1), self.generateDebugger());
        Debuggable debuggable = self.generateDebugger();

        return new ArmSubsystem(armInput, armControl, debuggable);

    }
}
