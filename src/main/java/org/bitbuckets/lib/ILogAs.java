package org.bitbuckets.lib;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.Map;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, ShuffleboardContainer con, IValueTuner<ProcessMode> mode);

    ILogAs<Boolean> BOOLEAN = (k, c, m) -> {
        var e = c
                .add(k, false)
                .withWidget(BuiltInWidgets.kBooleanBox)
                .getEntry();


        return (a) -> {
           if (e.exists() && e.isValid()) {
               e.setBoolean(a);
           } else {
               System.out.println("BAD NT");
           }
        };
    };

    ILogAs<Double> DOUBLE = (key,con, m) -> {
        var e = con.add(key, 0.0).withWidget(BuiltInWidgets.kTextView).getEntry();

        System.out.println(e.getTopic().getInfo().type);
        return (a) -> {
            if (e.exists() && e.isValid()) {
                e.setDouble(a);
            } else {
                System.out.println("BAD NT");
            }
        };
    };

    ILogAs<Double> DOUBLE_GRAPH = (key,con, m) -> {
        var e = con
                .add(key, 0.0)
                .withWidget(BuiltInWidgets.kGraph)
                .getEntry();

        return (a) -> {
            if (e.exists() && e.isValid()) {
                e.setDouble(a);
            } else {
                System.out.println("BAD NT");
            }
        };
    };

    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {
        return (k,c, m) -> {
            var e = c
                    .add(k, "default data")
                    .getEntry();

            return t -> e.setString(t.name());
        };
    }


}
