package org.bitbuckets.drive.balance;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.log.IDebuggable;

public class ExperimentalBalanceControl implements HasLifecycle, HasLogLoop {

    static final double TICKS_OVER_TILT_THRESHOLD = 0.4;  //?


    final BuiltInAccelerometer accelerometer = new BuiltInAccelerometer();
    final IDebuggable debuggable;

    public ExperimentalBalanceControl(IProcess process) {
        debuggable = process.getDebuggable();
    }

    //Stolen
    public double getPitch() {
        return Math.atan2((-accelerometer.getX()),
                Math.sqrt(accelerometer.getY() * accelerometer.getY() + accelerometer.getZ() * accelerometer.getZ())) * 57.3;
    }

    public double getRoll() {
        return Math.atan2(accelerometer.getY(), accelerometer.getZ()) * 57.3;
    }


    //We only want pitch so this is dumb
    public double getTilt() {
        double pitch = getPitch();
        double roll = getRoll();
        if ((pitch + roll) >= 0) {
            return Math.sqrt(pitch * pitch + roll * roll);
        } else {
            return -Math.sqrt(pitch * pitch + roll * roll);
        }
    }

    public int secondsToTicks(double time) {
        return (int) (time * 50);
    }

    int state = 0;
    int currentTicksOverTickThreshold = 0;

    final double robotSpeedSlow = 0.3;
    final double robotSpeedFast = 0.5;

    // routine for automatically driving onto and engaging the charge station.
    // returns a value from -1.0 to 1.0, which left and right motors should be set
    // to.
    public ChassisSpeeds autoBalanceRoutine() {
        switch (state) {
            // drive forwards to approach station, exit when tilt is detected
            case 0:
                if (getTilt() > 13) {
                    currentTicksOverTickThreshold++;
                }
                if (currentTicksOverTickThreshold > secondsToTicks(TICKS_OVER_TILT_THRESHOLD)) {
                    state = 1;
                    currentTicksOverTickThreshold = 0;
                    return new ChassisSpeeds(robotSpeedSlow, 0, 0);
                }
                return new ChassisSpeeds(robotSpeedFast, 0,0);
            // driving up charge station, drive slower, stopping when level
            case 1:
                if (getTilt() < 13) {
                    currentTicksOverTickThreshold++;
                }
                if (currentTicksOverTickThreshold > secondsToTicks(TICKS_OVER_TILT_THRESHOLD)) {
                    state = 2;
                    currentTicksOverTickThreshold = 0;

                    return new ChassisSpeeds(); //Stop moving
                }
                return new ChassisSpeeds(robotSpeedSlow, 0, 0);
            // on charge station, stop motors and wait for end of auto
            case 2:
                if (Math.abs(getTilt()) <= 13.0 / 2.0) { //Become more accurate
                    currentTicksOverTickThreshold++;
                }
                if (currentTicksOverTickThreshold > secondsToTicks(TICKS_OVER_TILT_THRESHOLD)) {
                    state = 4;
                    currentTicksOverTickThreshold = 0;
                    return new ChassisSpeeds();
                }
                if (getTilt() >= 13) {
                    return new ChassisSpeeds(0.1, 0, 0);
                } else if (getTilt() <= -13) {
                    return new ChassisSpeeds(-0.1, 0, 0);
                }
            case 3:
                return new ChassisSpeeds();
        }
        return new ChassisSpeeds();
    }

    @Override
    public void teleopInit() {
        currentTicksOverTickThreshold = 0;
    }

    @Override
    public void logLoop() {
        debuggable.log("state", state);
        debuggable.log("currentTicksOverTickThreshold", currentTicksOverTickThreshold);
    }
}

