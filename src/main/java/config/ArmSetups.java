package config;

import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SwapSetup;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.control.PIDCalculatorSetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

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
            MockingUtil.noops(IMotorController.class)
    );
    ISetup<IMotorController> UPPER_ARM = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    MotorIds.UPPER_ARM_ID,
                    Arm.UPPER_CONFIG,
                    Arm.UPPER_PID,
                    Optional.empty()
            ),
            MockingUtil.noops(IMotorController.class)

    );
    ISetup<IMotorController> GRIPPER_JOINT = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    MotorIds.GRIPPER_ARM_ID,
                    Arm.GRIPPER_CONFIG,
                    Arm.GRIPPER_PID,
                    Optional.empty()
            ),
            MockingUtil.noops(IMotorController.class)

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
            GRIPPER_JOINT
    );

}
