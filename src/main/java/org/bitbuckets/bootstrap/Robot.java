package org.bitbuckets.bootstrap;

import config.Mattlib;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import org.bitbuckets.auto.RobotEvent;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessMode;
import org.bitbuckets.lib.core.Path;
import org.bitbuckets.lib.process.SimpleProcess;

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

        builtProcess = new SimpleProcess(isReal(), new Path(new String[] {"root"}));
        try {
            buildRobot.build(builtProcess);

        } catch (Exception e) {
            DriverStation.reportError(e.getLocalizedMessage(), e.getStackTrace());
        }

    }

    @Override
    public void robotPeriodic() {
        builtProcess.loop();


        if (Mattlib.DEFAULT_MODE == ProcessMode.DEBUG) {
            builtProcess.logLoop();
        }
    }


    @Override
    public void autonomousPeriodic() {
        builtProcess.onRobotEvent(RobotEvent.AUTO_PERIODIC);
    }

    @Override
    public void teleopPeriodic() {
        builtProcess.onRobotEvent(RobotEvent.TELEOP_PERIODIC);
    }

    @Override
    public void autonomousInit() {
        builtProcess.onRobotEvent(RobotEvent.AUTO_INIT);
    }

    @Override
    public void teleopInit() {
        builtProcess.onRobotEvent(RobotEvent.TELEOP_INIT);
    }





}
