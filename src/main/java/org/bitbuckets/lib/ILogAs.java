package org.bitbuckets.lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.log.*;
import org.bitbuckets.lib.process.RegisterType;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.concurrent.CompletableFuture;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, Path p, IDoWhenReady whenReady, IValueTuner<ProcessMode> mode);

    ILogAs<Boolean> BOOLEAN = (k, p, ct, m) -> {
        CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
            return a.add(k, false).getEntry();
        }, RegisterType.LOG);

        return new BooleanLoggable(
                onReady,
                new BooleanLogEntry(DataUtil.LOG, p.getAsTablePath())
        );

    };


    ILogAs<Double> DOUBLE = (key,p, ct, m) -> {
        CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
            return a.add(key, 0).getEntry();
        }, RegisterType.LOG);

        return new DoubleLoggable(
                onReady,
                new DoubleLogEntry(DataUtil.LOG, p.getAsTablePath())
        );
    };

    ILogAs<Pose2d> POSE = (k,p, c,m) -> {

        CompletableFuture<GenericEntry> onReady = c.doWhenReady(a -> {

            return a.add(k, new double[] {0,0,0}).getEntry();
        }, RegisterType.LOG);

        return new PoseLoggable(new DoubleArrayLogEntry(DataUtil.LOG, k), onReady);
    };

    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {
        return (k,p, ct, m) -> {
            CompletableFuture<GenericEntry> onReady = ct.doWhenReady(a -> {
                return a.add(k, "default enum").getEntry();
            }, RegisterType.LOG);

            return new EnumLoggable<>(
                    onReady,
                    new StringLogEntry(
                            DataUtil.LOG,
                            p.getAsTablePath()
                    )
            );
        };
    }


}
