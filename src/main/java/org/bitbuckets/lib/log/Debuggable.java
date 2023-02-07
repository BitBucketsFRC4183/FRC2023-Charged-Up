package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.controlsds.sds.SwerveModule;

/**
 * It's like system.out + SmartDashboard.put
 */
public interface Debuggable {

    void out(String data);

    void log(String key, double number);
    void log(String key, String word);
    void log(String key, Enum<?> num);
    void log(String key, boolean data);
    void log(String key, Pose3d pose3);
    void log(String key, Pose2d pose2);
    void log(String key, Translation3d trans3);
    void log(String key, Translation2d trans2);
    void log(String key, SwerveModulePosition[] positions);
    void log(String key, SwerveModuleState[] states);

}
