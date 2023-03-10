package config;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.numbers.N1;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SwapSetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.hardware.IAbsoluteEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.ctre.CANCoderAbsoluteSetup;
import org.bitbuckets.lib.vendor.noops.NoopsAbsoluteEncoder;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.sim.dc.SimInertiaConfig;
import org.bitbuckets.lib.vendor.spark.SparkSetup;
import org.bitbuckets.lib.vendor.thrifty.ThriftyEncoderSetup;

import java.util.Optional;

public interface ArmSetups {

    SparkSetup LOWER_ARM_FOLLOWER = new SparkSetup(
            MotorIds.LOWER_ARM_FOLLOWER,
            Arm.LOWER_CONFIG_FOLLOWER,
            Arm.LOWER_PID,
            Optional.empty()
    );
    ISetup<IMotorController> LOWER_ARM = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    MotorIds.LOWER_ARM_ID_1,
                    Arm.LOWER_CONFIG,
                    Arm.LOWER_PID,
                    Optional.of(LOWER_ARM_FOLLOWER)
            ),
            new DCSimSetup(
                    Arm.LOWER_CONFIG,
                    new SimInertiaConfig(0.025, Matrix.mat(N1.instance, N1.instance).fill(1)),
                    Arm.LOWER_PID
            )
    );
    ISetup<IMotorController> UPPER_ARM = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    MotorIds.UPPER_ARM_ID,
                    Arm.UPPER_CONFIG,
                    Arm.UPPER_PID,
                    Optional.empty()
            ),
            new DCSimSetup(
                    Arm.UPPER_CONFIG,
                    new SimInertiaConfig(0.025, Matrix.mat(N1.instance, N1.instance).fill(1)),
                    Arm.UPPER_PID
            )

    );
    ISetup<IMotorController> GRIPPER_JOINT = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    MotorIds.GRIPPER_ARM_ID,
                    Arm.GRIPPER_CONFIG,
                    Arm.GRIPPER_PID,
                    Optional.empty()
            ),
            new DCSimSetup(
                    Arm.GRIPPER_CONFIG,
                    new SimInertiaConfig(0.005, Matrix.eye(Nat.N1())),
                    Arm.GRIPPER_PID
            )

    );


    ISetup<IPIDCalculator> LOWER_PID = new SwapSetup<>(
            MockingUtil.noops(IPIDCalculator.class),
            new PIDCalculatorSetup(Arm.LOWER_PID),
            new PIDCalculatorSetup(Arm.LOWER_SIMPID)
    );
    ISetup<IPIDCalculator> UPPER_PID = new SwapSetup<>(
            MockingUtil.noops(IPIDCalculator.class),
            new PIDCalculatorSetup(Arm.UPPER_PID),
            new PIDCalculatorSetup(Arm.UPPER_SIMPID)
    );



    ISetup<ArmControl> ARM_CONTROL = new ArmControlSetup(
            Arm.DOUBLE_JOINTED_FF,
            LOWER_ARM,
            UPPER_ARM,
            LOWER_PID,
            UPPER_PID,
            GRIPPER_JOINT);


}
