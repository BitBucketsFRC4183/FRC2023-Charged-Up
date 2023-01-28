package org.bitbuckets.bootstrap;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.core.*;
import org.bitbuckets.robot.RobotContainer;
import org.bitbuckets.robot.RobotSetup;
import org.bitbuckets.robot.RobotStateControl;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

/**
 * Launchpoint for the robot (It's like the launchpoint for the robot or something)
 * Has all the bucketlib and advantagekit code in it so don't touch it unless you really need to
 */
public class Robot extends LoggedRobot {


    RobotContainer robotHandle;
    LoopDriver loopDriver;

    @Override
    public void robotInit() {

        //inner setup script

        Logger logger = Logger.getInstance();

        logger.recordMetadata("ProjectName", BuildConstants.MAVEN_NAME);
        logger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE);
        logger.recordMetadata("GitSHA", BuildConstants.GIT_SHA);
        logger.recordMetadata("GitDate", BuildConstants.GIT_DATE);
        logger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH);
        logger.recordMetadata("Powered By", "MattLib");


        if (isReal()) {
            logger.addDataReceiver(new WPILOGWriter("/media/sda1/")); // Log to a USB stick
            logger.addDataReceiver(new NT4Publisher()); // Publish data to NetworkTables
            new PowerDistribution(1, PowerDistribution.ModuleType.kRev); // Enables power distribution logging
        } else {
            logger.addDataReceiver(new WPILOGWriter("analysis/"));
            logger.addDataReceiver(new NT4Publisher());
        }

        Logger.getInstance().start(); // Start logging! No more data receivers, replay sources, or metadata values may be added.

        loopDriver = new LoopDriver();
        IdentityDriver identityDriver = new IdentityDriver();
        LogDriver logDriver = new LogDriver(identityDriver);
        ErrorDriver errorDriver = new ErrorDriver(identityDriver);
        TuneableDriver tuneableDriver = new TuneableDriver(NetworkTableInstance.getDefault().getTable("ass"), identityDriver);

        ProcessPath rootPath = new ProcessPath(0, identityDriver, errorDriver, logDriver, loopDriver, tuneableDriver);
        RobotStateControl robotStateControl = new RobotStateControl(this);
        RobotSetup setup = new RobotSetup(robotStateControl);

        try {
            robotHandle = setup.build(rootPath);
        } catch (Exception e) {
            DriverStation.reportError("[BUCKET] Critical exception during setup: " + e.getLocalizedMessage(), e.getStackTrace());

        }


    }

    //periodics

    @Override
    public void robotPeriodic() {
        loopDriver.runAlways();
        robotHandle.robotPeriodic();
    }

    @Override
    public void teleopPeriodic() {
        robotHandle.teleopPeriodic();
    }

    @Override
    public void autonomousPeriodic() {
        robotHandle.autoPeriodic();
    }

    @Override
    public void simulationPeriodic() {
        loopDriver.runWhenSim();
    }


}
