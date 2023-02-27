package org.bitbuckets.arm;

import edu.wpi.first.math.util.Units;
import org.bitbuckets.arm.sim.ArmConfig;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.log.Debuggable;

import java.util.Optional;

public interface ArmConstants {

    int LOWER_ARM_1_MOTOR_ID = 9;
    int LOWER_ARM_2_MOTOR_ID = 10;
    int UPPER_ARM_MOTOR_ID = 11;
    // calculated gearRatio
    // Input to output- 5:1 4:1 3:1
    // Final gear different for         each arm; 16:48 for lower and 16:16 for upper

    //converts encoder rotations -> mechanism rotations (0.036)
    double LOWER_ARM_GEAR_RATIO = 1.0 / ((5.0 * 4.0 * 3.0) * (48. / 16.));
    double UPPER_ARM_GEAR_RATIO = 1.0 / ((5.0 * 5.0 * 4.0) * (16. / 16.));


    double LOWER_JOINT_LENGTH = 0.67;
    double UPPER_JOINT_LENGTH = 0.96; // including gripper


    //mainly for arm sim
    double UPPER_JOINT_WIDTH = 0.0254;
    double LOWER_JOINT_WIDTH = 0.0508;

    //in rotations
    double LOWER_ARM_FORWARD_LIMIT_MECHANISM = 0.98;
    double LOWER_ARM_REVERSE_LIMIT_MECHANISM = -0.494361111111;

    double LOWER_ARM_FORWARD_LIMIT_ENCODER = LOWER_ARM_FORWARD_LIMIT_MECHANISM / LOWER_ARM_GEAR_RATIO;
    double LOWER_ARM_REVERSE_LIMIT_ENCODER = LOWER_ARM_REVERSE_LIMIT_MECHANISM / LOWER_ARM_GEAR_RATIO;

    //in rotations as well
    double UPPER_ARM_FORWARD_LIMIT_MECHANISM = 1.041;
    double UPPER_ARM_REVERSE_LIMIT_MECHANISM = -3.75;

    double UPPER_ARM_FORWARD_LIMIT_ENCODER = UPPER_ARM_FORWARD_LIMIT_MECHANISM / UPPER_ARM_GEAR_RATIO;
    double UPPER_ARM_REVERSE_LIMIT_ENCODER = UPPER_ARM_REVERSE_LIMIT_MECHANISM / UPPER_ARM_GEAR_RATIO;

    MotorConfig LOWER_CONFIG = new MotorConfig(
            ArmConstants.LOWER_ARM_GEAR_RATIO,
            1,
            1,
            false,
            true,
            20.0,
            false,
            false,
            Optional.empty()
    );
    MotorConfig LOWER_CONFIG_FOLLOWER = new MotorConfig(
            ArmConstants.LOWER_ARM_GEAR_RATIO,
            1,
            1,
            true,
            true,
            20.0,
            false,
            false,
            Optional.empty()
    );
    MotorConfig UPPER_CONFIG = new MotorConfig(
            ArmConstants.UPPER_ARM_GEAR_RATIO,
            1,
            1,
            true,
            true,
            20.0,
            false,
            false,
            Optional.empty()
    );

    PIDConfig LOWER_PID = new PIDConfig(0.3, 0, 0, 0);
    PIDConfig UPPER_PID = new PIDConfig(0.3, 0, 0, 0);

    PIDConfig LOWER_SIMPID = new PIDConfig(1.0, 0, 0, 0);
    PIDConfig UPPER_SIMPID = new PIDConfig(5.0, 0, 0, 0);

    double SIM_OFFSET = 0.25;

    ArmConfig LOWER_ARM = new ArmConfig(
            0.686,
            1.036005,
            Units.rotationsToRadians(LOWER_ARM_FORWARD_LIMIT_MECHANISM),
            Units.rotationsToRadians(LOWER_ARM_REVERSE_LIMIT_MECHANISM),
            true
    );

    ArmConfig UPPER_ARM = new ArmConfig(
            0.84,  //not incl. gripper length
            1.474175, //not including gripper mass
            Units.rotationsToRadians(UPPER_ARM_FORWARD_LIMIT_MECHANISM),
            Units.rotationsToRadians(UPPER_ARM_REVERSE_LIMIT_MECHANISM),
            false
    );
    //change with accurate numbers


    double LOWER_MOI = 0.08;

    double LOWER_CGRADIUS = 0.4318;

    double UPPER_MOI = 0.99647313522;

    double UPPER_CGRADIUS = 0.4318;




    // will most likely change
    double GRABBER_LENGTH = 0.1524;

    double FFUPPER_ARM_LENGTH = UPPER_JOINT_LENGTH + GRABBER_LENGTH;

    double GRABBER_MASS = 0;


    // Feel free to change to make arms faster/slower for input
    double CONTROL_JOINT_OUTPUT = 1;

    /*
    Starting Configuration
    0 = Horizontal
    lowerJoint = 90
    upperJoint = 0
     */

    // Neo Brushless Motor Relative Encoder: 42 counts per revolution

    // all the numbers below are made up, not actual numbers we will use for competition
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

    //wrist + elbow for ff purposes

          //  double  elbowCgRadius =
          //  (UPPER_CONFIG.cgRadius * UPPER_ARM.armMass)
            //        + (UPPER_ARM.lengthMeters + config.wrist().cgRadius()) * config.wrist().mass()
            // / (UPPER_ARM.armMass + config.wrist().mass());
   // double elbowMoi =
   //         UPPER_ARM.armMass * Math.pow(UPPER_CGRADIUS - elbowCgRadius, 2.0)
      //              + config.wrist().mass()
       //             * Math.pow(
         //           UPPER_ARM.lengthMeters + config.wrist().cgRadius() - elbowCgRadius, 2.0);

    double FFUPPER_ARM_MASS = UPPER_ARM.armMass + GRABBER_MASS;


}
