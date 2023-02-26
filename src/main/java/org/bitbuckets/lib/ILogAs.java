package org.bitbuckets.lib;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import org.bitbuckets.lib.log.ILoggable;
import org.bitbuckets.lib.tune.IValueTuner;

import java.util.Map;

public interface ILogAs<T> {

    ILoggable<T> generate(String key, ShuffleboardContainer con, IValueTuner<ProcessMode> mode);

    ILogAs<Boolean> BOOLEAN = (k, c, m) -> {
        /*var e = c
                .add(k, false)
                .withWidget(BuiltInWidgets.kBooleanBox)
                .getEntry();

        e.setBoolean(false);*/

        return a -> {};
    };

    ILogAs<Double> DOUBLE = (key,con, m) -> {


        var e = con.add(key, 0.0).getEntry();
        System.out.println(con.getTitle() + "|" + key + "|" + e.getTopic().getName());



        e.setDouble(0.0);

        return a -> {};
    };

    ILogAs<Double> DOUBLE_GRAPH = (key,con, m) -> {
        /*var e = con
                .add(key, 0.0)
                .withWidget(BuiltInWidgets.kGraph)
                .getEntry();

        e.setDouble(0.0);

        return e::setDouble;*/
        return a -> {};
    };

    ILogAs<Double> DOUBLE_ANGLE = (key, con,m) -> {
        /*var e = con
                .add(key, 0.0)
                .withWidget(BuiltInWidgets.kGyro)
                .getEntry();

        e.setDouble(0.0);

        return e::setDouble;*/

        return a -> {};
    };

    ILogAs<double[]> PID_OUT_GRAPH = (key,con,m) -> {
        var e = con
                .add(key, new double[] {0,0,0})
                .withWidget(BuiltInWidgets.kGraph)
                .getEntry();



        return e::setDoubleArray;
    };

    static ILogAs<Double> DOUBLE_BAR(double min, double max) {
        return (key,con, m) -> {
            var e = con
                    .add(key, 0.0)
                    .withWidget(BuiltInWidgets.kNumberBar)
                    .withProperties(Map.of("min", min, "max", max))
                    .getEntry();

            return e::setDouble;
        };
    }

    //this is a silly type hack, pls fix
    static <T extends Enum<T>> ILogAs<T> ENUM(Class<T> clazz) {
        return (k,c, m) -> {
            var e = c
                    .add(k, "default data")
                    .getEntry();

            return t -> e.setString(t.name());
        };
    }


}
