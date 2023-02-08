package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.core.IdentityDriver;
import org.littletonrobotics.junction.Logger;

public class LogDriver implements ILogDriver {

    final Logger logger;
    final IdentityDriver identityDriver;

    public LogDriver(Logger logger, IdentityDriver identityDriver) {
        this.logger = logger;
        this.identityDriver = identityDriver;
    }



    @Override
    public ILoggable<String> generateStringLoggable(int id, String keyname) {
        String computed = identityDriver.fullPath(id) + keyname;

        return a -> logger.recordOutput(computed, a);
    }

    //TODO use sendablechooser style stuff
    @Override
    public <T extends Enum<T>> ILoggable<T> generateEnumLoggable(int id, Class<T> clazz, String keyname) {
        String computed = identityDriver.fullPath(id) + keyname;

        return a -> logger.recordOutput(computed, a.name());
    }

    @Override
    public ILoggable<double[]> generateMultiLoggable(int id, String... keys) {
        ILoggable<double[]> lg = a -> {
            for (int i = 0; i < keys.length; i++) {
                logger.recordOutput(identityDriver.fullPath(id) + keys[i], a[i]);
            }
        };

        int len = keys.length;
        double[] initData = new double[len];


        for (int i = 0; i < len; i++) {
            initData[i] = 0.0;
        }

        lg.log(initData);

        return lg;
    }

    @Override
    public ILoggable<Boolean> generateBoolLoggable(int id, String key) {
        String computed = identityDriver.fullPath(id) + key;

        return a -> logger.recordOutput(computed, a);
    }

    @Override
    public ILoggable<Translation2d[]> generateTranslationLogger(int id, String... key) {
        String computed = identityDriver.fullPath(id) + key;
        String x = computed + "-x";
        String y = computed + "-y";

        return a -> {
            for (int i = 0; i < key.length; i++) {
                logger.recordOutput(x + "-" + key[i], a[i].getX());
                logger.recordOutput(y + "-" + key[i], a[i].getY());
                logger.recordOutput(computed + "-vector-" + i, a[i].getNorm());
            }
        };
    }

    @Override
    public ILoggable<SwerveModuleState[]> generateSwerveLogger(int id, String key) {
        String computed = identityDriver.fullPath(id) + key;

        return a -> logger.recordOutput(computed, a);
    }

    @Override
    public Debuggable generateDebugger(int id) {
        String computed = identityDriver.fullPath(id);

        return new Debuggable() {
            @Override
            public void out(String data) {
                //TODO buffer
                logger.recordOutput(computed + "logs", data);
            }

            @Override
            public void log(String key, double number) {
                logger.recordOutput(computed + key, number);
            }

            @Override
            public void log(String key, String word) {
                logger.recordOutput(computed + key, word);
            }

            @Override
            public void log(String key, Enum<?> num) {
                logger.recordOutput(computed + key, num.toString());
            }

            @Override
            public void log(String key, boolean data) {
                logger.recordOutput(computed + key, data);
            }

            @Override
            public void log(String key, Pose3d pose3) {
                logger.recordOutput(computed + key, pose3);
            }

            @Override
            public void log(String key, Pose2d pose2) {
                logger.recordOutput(computed + key, pose2);
            }

            @Override
            public void log(String key, Translation3d trans3) {


                logger.recordOutput("WARNING", "cannot log positions because i havent added them yet");
            }

            @Override
            public void log(String key, Translation2d trans2) {

                logger.recordOutput("WARNING", "cannot log translations because i havent added them yet");
            }

            @Override
            public void log(String key, SwerveModulePosition[] positions) {
                //Do nothing, not implemented yet

                logger.recordOutput("WARNING", "cannot log positions because i havent added them yet");
            }

            @Override
            public void log(String key, SwerveModuleState[] states) {
                logger.recordOutput(key, states);
            }
        };
    }
}
