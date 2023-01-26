package org.bitbuckets.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import org.bitbuckets.lib.hardware.PIDIndex;

public interface RobotConstants {

    double WIDTH = 0.6096 / 2; //width of bot ?
    double BASE = 0.7712 / 2; //base of bot ?
    double WHEEL_DIAMETER_METERS = 0.10033;
    double WHEEL_CIRCUMFERENCE_METERS = WHEEL_DIAMETER_METERS * Math.PI;

    SwerveDriveKinematics KINEMATICS = new SwerveDriveKinematics(
            new Translation2d(WIDTH, BASE),
            new Translation2d(WIDTH, -BASE),
            new Translation2d(-WIDTH, BASE),
            new Translation2d(-WIDTH, -BASE)
    );

    double[] DEFAULT_PID_CONSTANTS = PIDIndex.CONSTANTS(0,0,0,0,0);


    int SET_MAXCONFIGTIME_MS = 100;

}