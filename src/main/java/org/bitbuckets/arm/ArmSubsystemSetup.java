package org.bitbuckets.arm;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.arm.sim.SimArmSetup;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.control.ProfiledPIDFSetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public class ArmSubsystemSetup implements ISetup<ArmSubsystem> {

    final AutoSubsystem autoSubsystem;
    final OperatorInput operatorInput;
    final boolean isEnabled;

    public ArmSubsystemSetup(AutoSubsystem autoSubsystem, OperatorInput operatorInput, boolean isEnabled) {
        this.autoSubsystem = autoSubsystem;
        this.operatorInput = operatorInput;
        this.isEnabled = isEnabled;
    }

    @Override
    public ArmSubsystem build(IProcess self) {
        if (!isEnabled) {
            return MockingUtil.buddy(ArmSubsystem.class);
        }

        ISetup<IMotorController> lowerArm1;
        ISetup<IMotorController> lowerArm2;
        ISetup<IMotorController> upperArm;

        ISetup<IPIDCalculator> lowerPID = new PIDCalculatorSetup(self.isReal() ? ArmConstants.LOWER_PID : ArmConstants.LOWER_SIMPID);
        ISetup<IPIDCalculator> upperPID = new PIDCalculatorSetup(self.isReal() ? ArmConstants.UPPER_PID : ArmConstants.UPPER_SIMPID);


        if (self.isReal()) {
            lowerArm1 = new SparkSetup(9, ArmConstants.LOWER_CONFIG, ArmConstants.LOWER_PID);
            lowerArm2 = new SparkSetup(10, ArmConstants.LOWER_CONFIG_FOLLOWER, ArmConstants.LOWER_PID);
            upperArm = new SparkSetup(11, ArmConstants.UPPER_CONFIG, ArmConstants.UPPER_PID);
        } else {

            Mechanism2d mech = new Mechanism2d(3, 3);
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

        }

        ArmControlSetup armControlSetup = new ArmControlSetup(
                lowerArm1,
                lowerArm2,
                upperArm,
                lowerPID,
                upperPID
        );

        ArmControl armControl = self.childSetup("arm-control", armControlSetup);

        return new ArmSubsystem(operatorInput, armControl, self.getDebuggable(), autoSubsystem);

    }
}
