package config;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.bitbuckets.arm.ArmDynamics;
import org.bitbuckets.arm.config.ArmJointConfig;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.hardware.OptimizationMode;

import java.util.Optional;

public interface Arm {


    //Need to get this value, (0.1) is wrong
    double GRIPPER_SETPOINT_MOTOR_ROTATIONS = 135;

    /**
     * How "correct" the arm's position has to be until the arm subsystem considers it at the position
     * Poor man's cost function
     */
    double ARM_TOLERANCE_TO_MOVE_ON = 0.1;
    double GRAVITY_MSS = 9.80665;

    //quick note to any software devs like me who have no idea what the physics is going on
    //i think gearing is general term referring to reduction OR upgear
    //reduction reduces rotations and increases torque, upgear does the opposite
    //the gear ratio here just converts from encoder-viewed rotations to mechanism-viewed rotations

    double LOWER_ARM_REDUCTION = 5.0 * 5.0 * 3.0;
    double UPPER_ARM_REDUCTION = 4.0 * 4.0 * 4.0 * 3.0;

    double LOWER_ARM_BELT = 48.0 / 16.0; //3 to 1
    double UPPER_ARM_BELT = 1.0; //1 to 1

    //converts encoder rotations -> mechanism rotations (0.036)
    double LOWER_ARM_GEAR_RATIO = 1.0 / LOWER_ARM_REDUCTION / LOWER_ARM_BELT; //divide again since calculator math
    double UPPER_ARM_GEAR_RATIO = 1.0 / UPPER_ARM_REDUCTION / UPPER_ARM_BELT;

    double UPPER_ARM_OFFSET = 0.56;


    //FF

    //TODO these are wrong
    double LOWER_ARM_FORWARD_LIMIT_MECHANISM = 0.98;
    double LOWER_ARM_REVERSE_LIMIT_MECHANISM = -0.494361111111;
    double UPPER_ARM_FORWARD_LIMIT_MECHANISM = 1.041;
    double UPPER_ARM_REVERSE_LIMIT_MECHANISM = -3.75;




    ArmJointConfig LOWER_ARM = new ArmJointConfig(
            0.686,
            1.036005,
            Units.rotationsToRadians(LOWER_ARM_FORWARD_LIMIT_MECHANISM),
            Units.rotationsToRadians(LOWER_ARM_REVERSE_LIMIT_MECHANISM),
            0.48809660363,
            0.4318,
            true,
            DCMotor.getNEO(2).withReduction(LOWER_ARM_REDUCTION)
    );


    //TODO should include gripper
    ArmJointConfig UPPER_ARM = new ArmJointConfig(
            0.99,  //
            1.474175,
            Units.rotationsToRadians(UPPER_ARM_FORWARD_LIMIT_MECHANISM),
            Units.rotationsToRadians(UPPER_ARM_REVERSE_LIMIT_MECHANISM),
            1.08935696109,
            0.4318, //TODO i strongly believe this is wrong
            false,
            DCMotor.getNEO(1).withReduction(UPPER_ARM_REDUCTION)
    );

    //MOTORS

    MotorConfig LOWER_CONFIG_FOLLOWER = new MotorConfig(
            LOWER_ARM_GEAR_RATIO,
            1,
            1,
            false,
            true,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            false, OptimizationMode.GENERIC,
            DCMotor.getNEO(1)
    );
    MotorConfig LOWER_CONFIG = new MotorConfig(
            LOWER_ARM_GEAR_RATIO,
            1,
            1,
            false,
            true,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            false, OptimizationMode.OFFBOARD_POS_PID,
            DCMotor.getNEO(1)
    );

    MotorConfig UPPER_CONFIG = new MotorConfig(
            UPPER_ARM_GEAR_RATIO,
            1,
            1,
            true,
            true,
            20.0,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            true, OptimizationMode.OFFBOARD_POS_PID,
            DCMotor.getNEO(1)

    );

    MotorConfig GRIPPER_CONFIG = new MotorConfig(
            1,
            1,
            1,
            true,
            false,
            30,
            Optional.empty(),
            Optional.of(-134.8),
            true,
            false,
            false, OptimizationMode.GENERIC,
            DCMotor.getNeo550(1)
    );


    //PID

    PIDConfig LOWER_PID = new PIDConfig(
            7,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig UPPER_PID = new PIDConfig(
            4.5,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig LOWER_SIMPID = new PIDConfig(
            1.0,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );
    PIDConfig UPPER_SIMPID = new PIDConfig(
            5.0,
            0,
            0,
            Optional.empty(),
            Optional.empty()
    );

    PIDConfig GRIPPER_PID = new PIDConfig(0.1, 0, 0, Optional.empty(), Optional.empty());


    ArmDynamics DYNAMICS = new ArmDynamics(
            LOWER_ARM,
            UPPER_ARM
    );

    Mechanism2d SIM_MECHANISM = new Mechanism2d(3, 3);
    MechanismRoot2d SIM_MECH_ROOT = SIM_MECHANISM.getRoot("base", 1.5, 1.5);

    MechanismLigament2d SIM_MECH_LOWER = SIM_MECH_ROOT.append(new MechanismLigament2d("lower-arm-sim", Arm.LOWER_ARM.length_meters, 90, 10, new Color8Bit(Color.kWhite)));
    MechanismLigament2d SIM_MECH_UPPER = SIM_MECH_LOWER.append(new MechanismLigament2d("upper-arm-sim", Arm.UPPER_ARM.length_meters, 90, 10, new Color8Bit(Color.kPurple)));



}
