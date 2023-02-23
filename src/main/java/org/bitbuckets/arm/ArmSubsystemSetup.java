package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.bitbuckets.arm.sim.ArmSimNew;
import org.bitbuckets.arm.sim.SimArmSetup;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.auto.AutoSubsystemSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final boolean isEnabled;
    AutoSubsystem autoSubsystem;

    public ArmSubsystemSetup(boolean isEnabled, AutoSubsystem autoSubsystem) {
        this.isEnabled = isEnabled;
        this.autoSubsystem = autoSubsystem;
    }

    @Override
    public ArmSubsystem build(ProcessPath self) {
        if (!isEnabled) {
            return MockingUtil.buddy(ArmSubsystem.class);
        }

        ISetup<IMotorController> lowerArm1;
        ISetup<IMotorController> lowerArm2;
        ISetup<IMotorController> upperArm;

        if (self.isReal()) {
            lowerArm1 = new SparkSetup(9, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID);
            lowerArm2 = new SparkSetup(10, ArmConstants.LOWER_CONFIG_FOLLOWER, ArmConstants.LOWER_PID);
            upperArm = new SparkSetup(11, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID);

        } else if (false) {

            Mechanism2d mech = new Mechanism2d(3, 3);
            // the mechanism root node
            MechanismRoot2d root = mech.getRoot("base", 1.5, 1.5);

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



        }

         else {
            Mechanism2d mech = new Mechanism2d(3, 3);
            // the mechanism root node
            MechanismRoot2d root = mech.getRoot("basebetter", 1.5, 2);

            MechanismLigament2d simLower = root.append(new MechanismLigament2d("lower-arm-sim", ArmConstants.LOWER_JOINT_LENGTH, 90, ArmConstants.LOWER_JOINT_WIDTH * 300, new Color8Bit(Color.kWhite)));
            MechanismLigament2d simUpper  =
                    simLower.append(
                            new MechanismLigament2d("upper-arm-sim", ArmConstants.UPPER_JOINT_LENGTH + ArmConstants.GRABBER_LENGTH, 90, ArmConstants.UPPER_JOINT_WIDTH * 300, new Color8Bit(Color.kPurple)));


            SmartDashboard.putData("sim-arm",mech);
            Debuggable debuggable = self.generateDebugger();

            var ff = new ArmFeedFordward();
            var armSimNew = new ArmSimNew(simUpper, simLower, ArmConstants.UPPER_CONFIG, ArmConstants.LOWER_CONFIG, ff, debuggable);
            lowerArm1 = armSimNew.getLowerArmSetup();
            upperArm = armSimNew.getUpperArmSetup();
            lowerArm2 = MockingUtil.noops(IMotorController.class);

            self.registerLogicLoop(armSimNew::updateLoopDeltaTwenty);
            self.registerLogLoop(armSimNew::logLoop);
        }

        ArmControlSetup armControlSetup = new ArmControlSetup(
                lowerArm1,
                lowerArm2,
                upperArm
        );

        Debuggable debuggable = self.generateDebugger();

        ArmControl armControl = armControlSetup.build(self.addChild("arm-control"));
        ArmInput armInput = new ArmInput(new Joystick(1), self.generateDebugger());

        return new ArmSubsystem(armInput, armControl, debuggable, autoSubsystem);

    }
}
