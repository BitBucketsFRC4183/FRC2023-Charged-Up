package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Translation2d;
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

        ILoggable<T> enumLog =  a -> logger.recordOutput(computed, a.name());

        return enumLog;
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

        return a -> logger.recordOutput(key, a);
    }
}
