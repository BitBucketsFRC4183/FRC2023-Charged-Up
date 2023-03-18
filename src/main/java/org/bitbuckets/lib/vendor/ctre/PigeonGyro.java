package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.sensors.WPI_Pigeon2;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import org.bitbuckets.auto.RobotEvent;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLogLoop;
import org.bitbuckets.lib.hardware.IGyro;
import org.bitbuckets.lib.log.IDebuggable;

public class PigeonGyro implements IGyro, HasLogLoop, HasLifecycle {

    final WPI_Pigeon2 pigeon2; //Pidgeon values are based on startup. Anal.
    final IDebuggable debuggable;

    public PigeonGyro(WPI_Pigeon2 pigeon2, IDebuggable debuggable) {
        this.pigeon2 = pigeon2;
        this.debuggable = debuggable;
    }

    @Override
    public Rotation2d getRotation2d_initializationRelative() {

        return pigeon2.getRotation2d();
    }

    @Override
    public double getAllianceRelativeYaw_deg() {
        return pigeon2.getYaw();
    }

    @Override
    public double getAllianceRelativePitch_deg() {
        return pigeon2.getPitch() - pitchTare;
    }

    @Override
    public double getAllianceRelativeRoll_deg() {
        return pigeon2.getRoll() - rollTare;
        /*double[] data = new double[4];
        pigeon2.getAccumGyro(data); //fill data

        return data[0];*/
    }

    @Override
    public double getAccelerationZ() {
        return builtInAccelerometer.getZ();
    }


    //hack because the pidgeon doesnt let me reset these other values;
    double rollTare = 0;
    double pitchTare = 0;


    public void zero() {
        pigeon2.setYaw(0);

        rollTare += getAllianceRelativeRoll_deg();
        pitchTare += getAllianceRelativePitch_deg();

        //tare LMAO

    }



    BuiltInAccelerometer builtInAccelerometer = new BuiltInAccelerometer();

    @Override
    public void logLoop() {
        short[] data = new short[3];
        pigeon2.getBiasedAccelerometer(data);


        debuggable.log("accel-x", builtInAccelerometer.getX());
        debuggable.log("accel-y", builtInAccelerometer.getY());
        debuggable.log("accel-z", builtInAccelerometer.getZ());

    }

    @Override
    public void onRobotEvent(RobotEvent robotEvent) {
        if (robotEvent == RobotEvent.AUTO_INIT) {
            zero(); //no matter what, the robot starts at 0.
        }
    }
}
