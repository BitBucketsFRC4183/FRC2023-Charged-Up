package org.bitbuckets.bootstrap;

import config.Mattlib;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
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

        builtProcess = new SimpleProcess(isReal(), new Path(new String[]{"root"}));
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
        builtProcess.autonomousPeriodic();
    }

    @Override
    public void teleopPeriodic() {
        builtProcess.teleopPeriodic();
    }

    @Override
    public void autonomousInit() {
        builtProcess.autonomousInit();
    }

    @Override
    public void teleopInit() {
        builtProcess.teleopInit();
    }

    @Override
    public void disabledInit() {
        builtProcess.disabledInit();
    }

    @Override
    public void disabledPeriodic() {
        builtProcess.disabledPeriodic();
    }
}
