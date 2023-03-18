package org.bitbuckets.lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.DataUtil;
import org.bitbuckets.lib.log.ILoggable;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, Path p);

    ILogAs<Boolean> BOOLEAN = (k, p) -> {
        String fullPath = p.getAsTablePath() + "log-" + k;

        NetworkTableEntry netEntry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(fullPath);
        BooleanLogEntry logEntry = new BooleanLogEntry(DataUtil.LOG, fullPath);

        netEntry.setBoolean(false);

        return a -> {
            netEntry.setBoolean(a);
            logEntry.append(a);
        };
    };


    ILogAs<Double> DOUBLE = (k,p) -> {
        String fullPath = p.getAsTablePath() + "log-"+ k;

        NetworkTableEntry netEntry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(fullPath);
        DoubleLogEntry logEntry = new DoubleLogEntry(DataUtil.LOG, fullPath);

        netEntry.setDouble(0.0);

        return a -> {
            netEntry.setDouble(a);
            logEntry.append(a);
        };
    };

    ILogAs<Pose2d> POSE = (k,p) -> {
        String fullPath = p.getAsTablePath() + "log-"+ k;
        NetworkTableEntry netEntry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(fullPath);
        DoubleArrayLogEntry logEntry = new DoubleArrayLogEntry(DataUtil.LOG, fullPath);

        netEntry.setDoubleArray(new double[] {0,0,0});

        return a -> {
            double[] array = new double[] {a.getX(), a.getY(), a.getRotation().getRadians()};

            netEntry.setDoubleArray(array);
            logEntry.append(array);
        };
    };

    ILogAs<String> STRING = (k,p) -> {
        String fullPath = p.getAsTablePath() + "log-"+ k;
        NetworkTableEntry netEntry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(fullPath);
        StringLogEntry logEntry = new StringLogEntry(DataUtil.LOG, fullPath);

        netEntry.setString("default str");

        return a -> {
            netEntry.setString(a);
            logEntry.append(a);
        };
    };





    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {


        return (k,p) -> {
            String fullPath = p.getAsTablePath() + "log-" + k;
            NetworkTableEntry netEntry = NetworkTableInstance.getDefault().getTable("mattlib").getEntry(fullPath);
            StringLogEntry logEntry = new StringLogEntry(DataUtil.LOG, fullPath);

            netEntry.setString("default enum");

            return a -> {
                netEntry.setString(a.name());
                logEntry.append(a.name());
            };
        };
    }


}
