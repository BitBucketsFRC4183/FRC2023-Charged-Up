package org.bitbuckets.arm;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.gripper.GripperConstants;
import org.bitbuckets.gripper.GripperControl;
import org.bitbuckets.gripper.GripperControlSetup;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.bitbuckets.arm.sim.SimArmSetup;
import org.bitbuckets.gripper.GripperInput;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.sim.dc.DCMotorConfig;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.ejml.simple.SimpleMatrix;

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

        ISetup<IMotorController> lowerArm1;
        ISetup<IMotorController> lowerArm2;
        ISetup<IMotorController> upperArm;
        ISetup<IMotorController> gripperJoint;

        if (self.isReal()) {
            lowerArm1 = new SparkSetup(9, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID);
            lowerArm2 = new SparkSetup(10, ArmConstants.LOWER_CONFIG_FOLLOWER, ArmConstants.LOWER_PID);
            upperArm = new SparkSetup(11, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID);
            gripperJoint = new SparkSetup(12, GripperConstants.GRIPPER_CONFIG, GripperConstants.GRIPPER_PID);

        } else {

            Mechanism2d mech = new Mechanism2d(3, 3);
            // the mechanism root node
            MechanismRoot2d root = mech.getRoot("base", 1.5, 0);

            MechanismLigament2d simLower = root.append(new MechanismLigament2d("lower-arm-sim", ArmConstants.LOWER_JOINT_LENGTH, 90, ArmConstants.LOWER_JOINT_WIDTH * 300, new Color8Bit(Color.kWhite)));
            MechanismLigament2d simUpper =
                    simLower.append(
                            new MechanismLigament2d("upper-arm-sim", ArmConstants.UPPER_JOINT_LENGTH + ArmConstants.GRABBER_LENGTH, 90, ArmConstants.UPPER_JOINT_WIDTH * 300, new Color8Bit(Color.kPurple)));


            SmartDashboard.putData("sim-arm", mech);

            lowerArm1 = new SimArmSetup(
                    ArmConstants.LOWER_CONFIG,
                    ArmConstants.LOWER_ARM,
                    ArmConstants.LOWER_SIMPID,
                    simLower
            );

            lowerArm2 = MockingUtil.noops(IMotorController.class);
            upperArm = new SimArmSetup(
                    ArmConstants.UPPER_CONFIG,
                    ArmConstants.UPPER_ARM,
                    ArmConstants.UPPER_SIMPID,
                    simUpper
            );
            gripperJoint = new DCSimSetup(GripperConstants.GRIPPER_CONFIG, GripperConstants.DC_GRIPPER_CONFIG, GripperConstants.GRIPPER_PID);

        }

        ArmControlSetup armControlSetup = new ArmControlSetup(
                lowerArm1,
                lowerArm2,
                upperArm
        );

        GripperControlSetup gripperControlSetup = new GripperControlSetup(gripperJoint);

        Debuggable debuggable = self.generateDebugger();
        ArmControl armControl = armControlSetup.build(self.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1), self.generateDebugger());

        GripperControl gripperControl = gripperControlSetup.build(self.addChild("gripper-control"));
        GripperInput gripperInput = new GripperInput(new Joystick(1));

        return new ArmSubsystem(armInput, armControl, gripperControl, gripperInput, debuggable);

    }
}
