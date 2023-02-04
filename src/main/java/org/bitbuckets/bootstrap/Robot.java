package org.bitbuckets.bootstrap;

import com.revrobotics.REVPhysicsSim;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.core.IdentityDriver;
import org.bitbuckets.lib.core.LogDriver;
import org.bitbuckets.lib.core.LoopDriver;
import org.bitbuckets.lib.core.NetworkPublisher;
import org.bitbuckets.lib.startup.SetupDriver;
import org.bitbuckets.lib.tune.TuneableDriver;
import org.bitbuckets.robot.RobotContainer;
import org.bitbuckets.robot.RobotSetup;
import org.bitbuckets.robot.RobotStateControl;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
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
            logger.addDataReceiver(new NetworkPublisher()); // Publish data to NetworkTables
            new PowerDistribution(1, PowerDistribution.ModuleType.kRev); // Enables power distribution logging
        } else {
            logger.addDataReceiver(new WPILOGWriter("analysis/"));
            logger.addDataReceiver(new NetworkPublisher());
        }

        logger.start(); // Start logging! No more data receivers, replay sources, or metadata values may be added.

        loopDriver = new LoopDriver();
        IdentityDriver identityDriver = new IdentityDriver();
        LogDriver logDriver = new LogDriver(logger, identityDriver);
        TuneableDriver tuneableDriver = new TuneableDriver(NetworkTableInstance.getDefault().getTable("RealOutputs/MattTuneables"), identityDriver);

        int consoleId = identityDriver.childProcess(0, "Console");
        SetupDriver setupDriver = new SetupDriver(identityDriver, logDriver, consoleId);
        ProcessPath rootPath = new ProcessPath(0, setupDriver, identityDriver, logDriver, loopDriver, tuneableDriver, isReal());
        RobotStateControl robotStateControl = new RobotStateControl(this);
        RobotSetup setup = new RobotSetup(robotStateControl);

        rootPath.registerLoop(robotStateControl, "stateControl");



        try {
            robotHandle = setup.build(rootPath);
        } catch (Exception e) {
            DriverStation.reportError("[BUCKET] Critical exception during setup: " + e.getLocalizedMessage(), e.getStackTrace());
            throw e;
        }


    }

    //periodics

    @Override
    public void robotPeriodic() {
        loopDriver.runPeriodic();
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
        REVPhysicsSim.getInstance().run();
    }


}
