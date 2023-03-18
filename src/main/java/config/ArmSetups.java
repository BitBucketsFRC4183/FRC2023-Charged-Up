package config;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.arm.ArmControl;
import org.bitbuckets.arm.ArmControlSetup;
import org.bitbuckets.arm.sim.SimArmControllerSetup;
import org.bitbuckets.arm.sim.SimArmCore;
import org.bitbuckets.arm.sim.SimArmCoreSetup;
import org.bitbuckets.arm.sim.SimJoint;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.SharedSetup;
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

    ISetup<SimArmCore> SIM_CORE = new SharedSetup<>(new SimArmCoreSetup(
            Arm.DYNAMICS,
            VecBuilder.fill(Math.PI / 2.0, 0.05, 0,0)
    ));

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
            new SimArmControllerSetup(
                    SIM_CORE,
                    Arm.SIM_MECH_LOWER,
                    SimJoint.SHOULDER,
                    Arm.LOWER_SIMPID
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
            new SimArmControllerSetup(
                    SIM_CORE,
                    Arm.SIM_MECH_UPPER,
                    SimJoint.ELBOW,
                    Arm.UPPER_SIMPID
            )

    );
    ISetup<IMotorController> GRIPPER_JOINT = new SwapSetup<>(
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

    ISetup<IPIDCalculator> PROFILED_LOWER_PID = new ProfiledPIDFCalculatorSetup(
            Arm.LOWER_PID,
            new TrapezoidProfile.Constraints(2,2)
    );

    ISetup<IPIDCalculator> PROFILED_UPPER_PID = new ProfiledPIDFCalculatorSetup(
            Arm.UPPER_PID,
            new TrapezoidProfile.Constraints(2,2)
    );
     */



    ISetup<ArmControl> ARM_CONTROL = new ArmControlSetup(
            Arm.DYNAMICS,
            LOWER_ARM,
            UPPER_ARM,
            PROFILED_LOWER_PID,
            PROFILED_UPPER_PID,
            GRIPPER_JOINT);


}
