package org.bitbuckets.lib.debug;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.ProcessMode;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class ShuffleDebuggable implements IDebuggable {

    final CompletableFuture<ShuffleboardContainer> container;
    final IValueTuner<ProcessMode> modeTuner;

    //incredibly stupid
    final ConcurrentMap<String, GenericEntry> cache = new ConcurrentHashMap<>();

    public ShuffleDebuggable(CompletableFuture<ShuffleboardContainer> container, IValueTuner<ProcessMode> modeTuner) {
        this.modeTuner = modeTuner;
        this.container = container;

        //TODO this is going to cause huge errors
        //container.add("debugged yet", false);
    }

    void consume(Consumer<ShuffleboardContainer> consumer) {
        if (container.isDone()) {
            consumer.accept(container.join());
        } else {
            container.thenAccept(consumer);
        }
    }

    @Override
    public void log(String key, double number) {
        if (modeTuner.readValue().level > ProcessMode.LOG_DEBUG.level) return;


        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, number).getEntry()).setDouble(number);
        });



    }

    @Override
    public void log(String key, String word) {
        if (modeTuner.readValue().level > ProcessMode.LOG_DEBUG.level) return;
        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, word).getEntry()).setString(word);
        });
    }

    @Override
    public void log(String key, Enum<?> num) {
        if (modeTuner.readValue().level > ProcessMode.LOG_DEBUG.level) return;

        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, num.name()).getEntry()).setString(num.name());
        });
    }

    @Override
    public void log(String key, boolean data) {
        if (modeTuner.readValue().level > ProcessMode.LOG_DEBUG.level) return;

        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, data).getEntry()).setBoolean(data);
        });

    }

    @Override
    public void log(String key, Pose3d pose3d) {
        double[] data = new double[7];
        data[0] = pose3d.getX();
        data[1] = pose3d.getY();
        data[2] = pose3d.getZ();
        data[3] = pose3d.getRotation().getQuaternion().getW();
        data[4] = pose3d.getRotation().getQuaternion().getX();
        data[5] = pose3d.getRotation().getQuaternion().getY();
        data[6] = pose3d.getRotation().getQuaternion().getZ();

        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, data).getEntry()).setDoubleArray(data);
        });

    }

    @Override
    public void log(String key, Pose2d pose2) {
        if (modeTuner.readValue().level > ProcessMode.LOG_DEBUG.level) return;

        double[] data = new double[3];
        data[0] = pose2.getX();
        data[1] = pose2.getY();
        data[2] = pose2.getRotation().getRadians();

        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, data).getEntry()).setDoubleArray(data);
        });


    }

    @Override
    public void log(String key, Translation3d trans3) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void log(String key, Translation2d trans2) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void log(String key, SwerveModulePosition[] positions) {


    }

    @Override
    public void log(String key, SwerveModuleState[] value) {
        if (modeTuner.readValue().level > ProcessMode.LOG_DEBUG.level) return;

        double[] data = new double[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            data[i * 2] = value[i].angle.getRadians();
            data[i * 2 + 1] = value[i].speedMetersPerSecond;
        }

        consume(a-> {
            cache.computeIfAbsent(key, k -> a.add(k, data).getEntry()).setDoubleArray(data);
        });

    }
}
