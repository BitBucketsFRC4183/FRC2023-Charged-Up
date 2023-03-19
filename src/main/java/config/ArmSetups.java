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
import org.bitbuckets.lib.control.ProfiledPIDFCalculatorSetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.sim.dc.DCSimSetup;
import org.bitbuckets.lib.vendor.sim.dc.SimInertiaConfig;
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
    ISetup<IMotorController> GRIPPER_WHEEL = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            //TODO add back motor when gripper is back
            /*new SparkSetup(
                    MotorIds.GRIPPER_ARM_ID,
                    Arm.GRIPPER_CONFIG,
                    Arm.GRIPPER_PID,
                    Optional.empty()
            ),*/
            MockingUtil.noops(IMotorController.class),
            new DCSimSetup(
                    Arm.GRIPPER_WHEEL_CONFIG,
                    new SimInertiaConfig(0.005, Matrix.eye(Nat.N1())),
                    Arm.GRIPPER_WHEEL_PID
            )

    );

    ISetup<IMotorController> GRIPPER_CLAW = new SwapSetup<>(
            MockingUtil.noops(IMotorController.class),
            new SparkSetup(
                    MotorIds.GRIPPER_CLAW_ID,
                    Arm.GRIPPER_CLAW_CONFIG,
                    Arm.GRIPPER_CLAW_PID,
                    Optional.empty()
            ),
            new DCSimSetup(
                    Arm.GRIPPER_CLAW_CONFIG,
                    new SimInertiaConfig(0.005, Matrix.eye(Nat.N1())),
                    Arm.GRIPPER_CLAW_PID
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


    ISetup<IPIDCalculator> PROFILED_LOWER_PID = new SwapSetup<>(
            MockingUtil.noops(IPIDCalculator.class),
            new ProfiledPIDFCalculatorSetup(Arm.LOWER_PID, Arm.LOWER_CONSTRAINT),
            new ProfiledPIDFCalculatorSetup(Arm.LOWER_SIMPID, Arm.LOWER_CONSTRAINT)
    );
    ISetup<IPIDCalculator> PROFILED_UPPER_PID = new SwapSetup<>(
            MockingUtil.noops(IPIDCalculator.class),
            new ProfiledPIDFCalculatorSetup(Arm.UPPER_PID, Arm.UPPER_CONSTRAINTS),
            new ProfiledPIDFCalculatorSetup(Arm.UPPER_SIMPID, Arm.UPPER_CONSTRAINTS)
    );

    /**
     * ISetup<IPIDCalculator> PROFILED_LOWER_PID = new ProfiledPIDFCalculatorSetup(
     * Arm.LOWER_PID,
     * new TrapezoidProfile.Constraints(2, 2)
     * );
     * <p>
     * ISetup<IPIDCalculator> PROFILED_UPPER_PID = new ProfiledPIDFCalculatorSetup(
     * Arm.UPPER_PID,
     * new TrapezoidProfile.Constraints(2, 2)
     * );
     */


    ISetup<ArmControl> ARM_CONTROL = new ArmControlSetup(
            Arm.DOUBLE_JOINTED_FF,
            LOWER_ARM,
            UPPER_ARM,
            PROFILED_LOWER_PID,
            PROFILED_UPPER_PID,
            GRIPPER_WHEEL,
            GRIPPER_CLAW);


}
