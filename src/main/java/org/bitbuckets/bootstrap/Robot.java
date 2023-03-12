package org.bitbuckets.bootstrap;

import config.Mattlib;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.TimedRobot;
import org.bitbuckets.auto.RobotEvent;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.LogRecord;
import org.bitbuckets.lib.process.DisableProcess;
import org.bitbuckets.lib.process.RootProcess;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Launchpoint for the robot (It's like the launchpoint for the robot or something)
 * Has all the bucketlib and advantagekit code in it so don't touch it unless you really need to
 */
public class Robot extends TimedRobot {


    final ISetup<Void> buildRobot;

    IProcess builtProcess;

    public Robot(ISetup<Void> buildRobot) {
        this.buildRobot = buildRobot;
    }

    @Override
    public void robotInit() {
        try {
            Thread.sleep(2000); //i dont know why this works, but if you dont do it networktables literally expldoes
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        try {
            if (Mattlib.SHOULD_FORCE_KILL) {
                builtProcess=new DisableProcess();
            } else {
                builtProcess = RootProcess.root();
            }

            buildRobot.build(builtProcess);

        } catch (Exception e) {
            DriverStation.reportError(e.getLocalizedMessage(), e.getStackTrace());
        }

    }

    boolean ran = false;

    @Override
    public void robotPeriodic() {
        if (!ran) {
            ran = true;
            builtProcess.ready();
        }

        Threads.setCurrentThreadPriority(true, 99); //stupid hack
        builtProcess.run();
    }

    @Override
    public void autonomousInit() {
        builtProcess.onPhaseChangeEvent(RobotEvent.AUTO_INIT);
    }

    @Override
    public void teleopInit() {
        builtProcess.onPhaseChangeEvent(RobotEvent.TELEOP_INIT);
    }





}
