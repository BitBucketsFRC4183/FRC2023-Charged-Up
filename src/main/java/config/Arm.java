package config;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import org.bitbuckets.arm.sim.ArmConfig;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.vendor.sim.dc.DCMotorConfig;
import org.ejml.simple.SimpleMatrix;

import java.util.Optional;

public interface Arm {
    //converts encoder rotations -> mechanism rotations (0.036)
    double LOWER_ARM_GEAR_RATIO = 1.0 / ((5.0 * 4.0 * 3.0) * (48. / 16.));
    MotorConfig LOWER_CONFIG_FOLLOWER = new MotorConfig(
            LOWER_ARM_GEAR_RATIO,
            1,
            1,
            true,
            true,
            20.0,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            Optional.empty()
    );
    MotorConfig LOWER_CONFIG = new MotorConfig(
            LOWER_ARM_GEAR_RATIO,
            1,
            1,
            false,
            true,
            20.0,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            Optional.empty()
    );
    double UPPER_ARM_GEAR_RATIO = 1.0 / ((5.0 * 5.0 * 4.0) * (16. / 16.));
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
            Optional.empty()
    );
    double LOWER_JOINT_LENGTH = 0.6731;
    double UPPER_JOINT_LENGTH = 0.9652; // including gripper
    //mainly for arm sim
    double UPPER_JOINT_WIDTH = 0.0254;
    double LOWER_JOINT_WIDTH = 0.0508;
    //in rotations
    double LOWER_ARM_FORWARD_LIMIT_MECHANISM = 0.98;
    double LOWER_ARM_FORWARD_LIMIT_ENCODER = LOWER_ARM_FORWARD_LIMIT_MECHANISM / LOWER_ARM_GEAR_RATIO;
    double LOWER_ARM_REVERSE_LIMIT_MECHANISM = -0.494361111111;
    ArmConfig LOWER_ARM = new ArmConfig(
            0.686,
            1.036005,
            Units.rotationsToRadians(LOWER_ARM_FORWARD_LIMIT_MECHANISM),
            Units.rotationsToRadians(LOWER_ARM_REVERSE_LIMIT_MECHANISM),
            true
    );
    double LOWER_ARM_REVERSE_LIMIT_ENCODER = LOWER_ARM_REVERSE_LIMIT_MECHANISM / LOWER_ARM_GEAR_RATIO;
    //in rotations as well
    double UPPER_ARM_FORWARD_LIMIT_MECHANISM = 1.041;
    double UPPER_ARM_FORWARD_LIMIT_ENCODER = UPPER_ARM_FORWARD_LIMIT_MECHANISM / UPPER_ARM_GEAR_RATIO;
    double UPPER_ARM_REVERSE_LIMIT_MECHANISM = -3.75;
    ArmConfig UPPER_ARM = new ArmConfig(
            0.84,  //not incl. gripper length
            1.474175, //not including gripper mass
            Units.rotationsToRadians(UPPER_ARM_FORWARD_LIMIT_MECHANISM),
            Units.rotationsToRadians(UPPER_ARM_REVERSE_LIMIT_MECHANISM),
            false
    );
    double UPPER_ARM_REVERSE_LIMIT_ENCODER = UPPER_ARM_REVERSE_LIMIT_MECHANISM / UPPER_ARM_GEAR_RATIO;
    PIDConfig LOWER_PID = new PIDConfig(0.3, 0, 0, Optional.empty(), Optional.empty());
    PIDConfig UPPER_PID = new PIDConfig(0.3, 0, 0, Optional.empty(), Optional.empty());
    PIDConfig LOWER_SIMPID = new PIDConfig(1.0, 0, 0, Optional.empty(), Optional.empty());
    PIDConfig UPPER_SIMPID = new PIDConfig(5.0, 0, 0, Optional.empty(), Optional.empty());
    TrapezoidProfile.Constraints LOWER_CONSTRAINT = new TrapezoidProfile.Constraints(2,2);
    TrapezoidProfile.Constraints UPPER_CONSTRAINTS = new TrapezoidProfile.Constraints(2,2);
    double LOWER_MOI = 0.08;
    double LOWER_CGRADIUS = 0.4318;
    double UPPER_MOI = 0.99647313522;
    double UPPER_CGRADIUS = 0.4318;
    // will most likely change
    double GRABBER_LENGTH = 0.1524;
    double FFUPPER_ARM_LENGTH = UPPER_JOINT_LENGTH + GRABBER_LENGTH;
    double GRABBER_MASS = 0;
    double FFUPPER_ARM_MASS = UPPER_ARM.armMass + GRABBER_MASS;
    // Feel free to change to make arms faster/slower for input
    double CONTROL_JOINT_OUTPUT = 1;
    double STORAGE_X = 0.3;
    double STORAGE_Y = -0.1;
    double PREPARE_X = 0.1;
    double PREPARE_Y = 0.6;
    double HUMAN_INTAKE_X = 0.5;
    double HUMAN_INTAKE_Y = 0.5;
    double LOW_NODE_X = 0.4;
    double LOW_NODE_Y = 0.4;
    double MID_NODE_X = 0.8;
    double MID_NODE_Y = 0.8;
    double HIGH_NODE_X = 0.8;
    double HIGH_NODE_Y = 0.8;
    double INTAKE_GROUND_X = 0.1;
    double INTAKE_GROUND_Y = 0.1;

    //Gripper Config
    MotorConfig GRIPPER_CONFIG = new MotorConfig(1, 1, 1, false, false, 10, Optional.empty(), Optional.empty(), true, true, Optional.empty());
    DCMotorConfig DC_GRIPPER_CONFIG = new DCMotorConfig(0.01, new Matrix<>(SimpleMatrix.identity(0)));
    PIDConfig GRIPPER_SIMPID = new PIDConfig(1.0, 0, 0, Optional.empty(), Optional.empty());
    PIDConfig GRIPPER_PID = new PIDConfig(0.1,0,0,Optional.empty(), Optional.empty());
    //Need to get this value, (0.1) is wrong
    double GRIPPER_SETPOINT_MOTOR_ROTATIONS = 135;
}
