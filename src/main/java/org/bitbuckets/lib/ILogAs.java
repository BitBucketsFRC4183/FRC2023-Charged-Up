package org.bitbuckets.lib;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.log.ScheduledLoggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, ShuffleboardContainer con, IValueTuner<ProcessMode> mode);

    ILogAs<Boolean> BOOLEAN = (k, conn, m) -> new ScheduledLoggable<>(
            TRACKER.SEX.schedule(
                    () -> conn.add(k, false)
                            .withWidget(BuiltInWidgets.kGraph)
                            .getEntry(),
                    TRACKER.LASTMS += 1,
                    TimeUnit.SECONDS
            )
    );


    class TRACKER {
        static final ScheduledExecutorService SEX = new ScheduledThreadPoolExecutor(2);
        static int LASTMS = 0;
    }



    ILogAs<Double> DOUBLE = (key,con, m) -> new ScheduledLoggable<>(
            TRACKER.SEX.schedule(
                    () -> con.add(key, 0.0)
                            .withWidget(BuiltInWidgets.kGraph)
                            .getEntry(),
                    TRACKER.LASTMS += 1,
                    TimeUnit.SECONDS
            )
    );

    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {
        return (k,c, m) -> {
            var e = c
                    .add(k, "default data")
                    .getEntry();

            return t -> e.setString(t.name());
        };
    }


}
