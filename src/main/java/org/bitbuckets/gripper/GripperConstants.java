package org.bitbuckets.gripper;

import edu.wpi.first.math.Matrix;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.vendor.sim.dc.DCMotorConfig;
import org.ejml.simple.SimpleMatrix;

import java.util.Optional;

public interface GripperConstants {

    int GRIPPER_MOTOR_ID = 11;
    MotorConfig GRIPPER_CONFIG = new MotorConfig(1, 1, 1, false, false, 10, true, true, Optional.empty());

    DCMotorConfig DC_GRIPPER_CONFIG = new DCMotorConfig(0.01, new Matrix<>(SimpleMatrix.identity(0)));
    PIDConfig GRIPPER_PID = new PIDConfig(0.1,0,0,0);

    //Need to get this value, (0.1) is wrong
    double GRIPPER_SETPOINT_MOTOR_ROTATIONS = 500;

}
