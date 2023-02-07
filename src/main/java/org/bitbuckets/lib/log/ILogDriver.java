package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * Generally these should automatically be loaded on the smartdashboard
 */
public interface ILogDriver {

    ILoggable<String> generateStringLoggable(int id, String keyname);

    /**
     * Will not initially be put into the smartdashboard, needs to have a call to .log to work
     * @param id
     * @param clazz
     * @param keyname
     * @return
     * @param <T>
     */
    <T extends Enum<T>> ILoggable<T> generateEnumLoggable(int id, Class<T> clazz, String keyname );
    ILoggable<double[]> generateMultiLoggable(int id, String... keys);
    ILoggable<Boolean> generateBoolLoggable(int id, String key);
    ILoggable<Double> generateDoubleLoggable(int id, String key);

    /**
     * Generates a translation logger with x y and vector normalized
     * @param id
     * @param key
     * @return
     */
    ILoggable<Translation2d[]> generateTranslationLogger(int id, String... key);
    ILoggable<SwerveModuleState[]> generateSwerveLogger(int id, String key);

    ILoggable<Pose2d> generatePoseLogger(int id, String key);
    ILoggable<Pose3d> generatePose3Logger(int id, String key);

}
