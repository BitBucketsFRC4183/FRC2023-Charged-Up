package org.bitbuckets.lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.log.EnumLoggable;
import org.bitbuckets.lib.log.FutureLoggable;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.PoseLoggable;
import org.bitbuckets.lib.process.RegisterType;
import org.bitbuckets.lib.tune.IValueTuner;

import java.lang.annotation.Documented;
import java.util.concurrent.CompletableFuture;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, IDoWhenReady whenReady, IValueTuner<ProcessMode> mode);

    ILogAs<Boolean> BOOLEAN = (k, ct, m) -> {
        CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
            return a.add(k, false).getEntry();
        }, RegisterType.LOG);

        return new FutureLoggable<>(onReady);
    };


    ILogAs<Double> DOUBLE = (key,ct, m) -> {
        CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
            return a.add(key, 0).getEntry();
        }, RegisterType.LOG);

        return new FutureLoggable<>(onReady);
    };

    ILogAs<Pose2d> POSE = (k,c,m) -> {

        CompletableFuture<GenericEntry> onReady = c.doWhenReady(a -> {

            return a.add(k, new double[] {0,0,0}).getEntry();
        }, RegisterType.LOG);

        return new PoseLoggable(onReady);
    };

    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {
        return (k,ct, m) -> {
            CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
                return a.add(k, "default enum").getEntry();
            }, RegisterType.LOG);

            return new EnumLoggable<>(onReady);
        };
    }

    static <T extends Enum<T>> ILogAs<T> ENUM_SIDEBAR(Class<T> clazz) {
        return (k,ct, m) -> {
            CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
                return a.add(k, "default enum").getEntry();
            }, RegisterType.SIDEBAR);

            return new FutureLoggable<>(onReady);
        };
    }


}
