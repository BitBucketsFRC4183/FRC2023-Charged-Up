package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class NoopsDebuggable implements IDebuggable {
    @Override
    public void log(String key, double number) {

    }

    @Override
    public void log(String key, String word) {

    }

    @Override
    public void log(String key, Enum<?> num) {

    }

    @Override
    public void log(String key, boolean data) {

    }

    @Override
    public void log(String key, Pose3d pose3) {

    }

    @Override
    public void log(String key, Pose2d pose2) {

    }

    @Override
    public void log(String key, Translation3d trans3) {

    }

    @Override
    public void log(String key, Translation2d trans2) {

    }

    @Override
    public void log(String key, SwerveModulePosition[] positions) {

    }

    @Override
    public void log(String key, SwerveModuleState[] states) {

    }
}
