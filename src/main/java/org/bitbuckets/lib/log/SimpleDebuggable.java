package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.ILogAs;
import org.bitbuckets.lib.IProcess;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SimpleDebuggable implements IDebuggable {


    final IProcess process;
    final Map<String, ILoggable> map = new HashMap<>();

    public SimpleDebuggable(IProcess process) {
        this.process = process;
    }

    @Override
    public void log(String key, double number) {

        map.computeIfAbsent(key, k-> {
            System.out.println("called for: " + k);
            return process.generateLogger(ILogAs.DOUBLE, k);
        } ).log(number);
    }

    @Override
    public void log(String key, String word) {
        map.computeIfAbsent(key, k-> process.generateLogger(ILogAs.STRING, k)).log(word);
    }

    @Override
    public void log(String key, Enum<?> num) {
        map.computeIfAbsent(key, k -> process.generateLogger(ILogAs.ENUM(Enum.class), k)).log(num);
    }

    @Override
    public void log(String key, boolean data) {
        map.computeIfAbsent(key, k -> process.generateLogger(ILogAs.BOOLEAN, k)).log(data);
    }

    @Override
    public void log(String key, Pose3d pose3) {
    }

    @Override
    public void log(String key, Pose2d pose2) {
        map.computeIfAbsent(key, k -> process.generateLogger(ILogAs.POSE, k)).log(pose2);
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
