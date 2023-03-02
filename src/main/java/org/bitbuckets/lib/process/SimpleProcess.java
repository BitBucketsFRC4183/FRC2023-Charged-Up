package org.bitbuckets.lib.process;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.lib.*;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.debug.IDebuggable;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SimpleProcess implements IProcess {

    final Set<String> alreadyUsedDashboardKeys = new HashSet<>();
    final Function<String, Boolean> canUse = alreadyUsedDashboardKeys::contains;

    final Path path;

    public SimpleProcess(Path path) {
        this.path = path;
    }

    @Override
    public Path getSelfPath() {
        return path;
    }

    @Override
    public <T> T childSetup(String key, ISetup<T> setup) {
        return setup.build(new SimpleProcess(path.append(key)));
    }

    @Override
    public <T> T siblingSetup(String key, ISetup<T> setup) {
        return setup.build(new SimpleProcess(path.sibling(key)));
    }

    @Override
    public IConsole getAssociatedConsole() {
        throw new UnsupportedOperationException("sorry not working rn");
    }

    @Override
    public IDebuggable getDebuggable() {
        return new IDebuggable() {
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
        };
    }

    @Override
    public <T> IValueTuner<T> generateTuner(ITuneAs<T> tuneDataType, String key, T dataWhenNotTuning) {
        return null;
    }

    @Override
    public <T> ILoggable<T> generateLogger(ILogAs<T> logDataType, String key) {
        return null;
    }

    @Override
    public void forceTo(ProcessMode mode) {

    }

    @Override
    public void registerLogLoop(HasLogLoop loop) {

    }

    @Override
    public void registerLogicLoop(HasLoop loop) {

    }

    @Override
    public void registerLifecycle(HasLifecycle lifecycle) {

    }

    @Override
    public void run() {

    }

    @Override
    public int componentQuantity() {
        return 0;
    }

    @Override
    public HasLifecycle offerInternalLifecycler() {
        return null;
    }
}
