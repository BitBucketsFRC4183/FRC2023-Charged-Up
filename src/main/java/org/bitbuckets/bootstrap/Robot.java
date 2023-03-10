package org.bitbuckets.bootstrap;

import com.revrobotics.REVPhysicsSim;
import config.Mattlib;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.LogRecord;
import org.bitbuckets.lib.log.ProcessConsole;
import org.bitbuckets.lib.process.ForceKillProcess;
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
                builtProcess=new ForceKillProcess();
            }
            builtProcess = RootProcess.root();
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

        StringBuilder report = new StringBuilder();
        Map<String, StringBuilder> thisIsBad = new HashMap<>();

        while (!ProcessConsole.QUEUE.isEmpty()) {
            LogRecord record = ProcessConsole.QUEUE.remove();

            String consolePost;
            if (record.exception == null) {
                var opt = record.key.getAsLastTwoPath();
                String use = opt.orElseGet(record.key::getTail);

                consolePost = format(
                        "[%s]: %s",
                        use,
                        record.info
                );
            } else {
                var opt = record.key.getAsLastTwoPath();
                String use = opt.orElseGet(record.key::getTail);

                consolePost = format(
                        "[%s] ERROR: %s (%s:%s)",
                        use,
                        record.exception.getLocalizedMessage(),
                        record.exception.getStackTrace()[0].getMethodName(),
                        record.exception.getStackTrace()[0].getLineNumber()
                );

            }

            report.append(consolePost).append("\n");
            thisIsBad.computeIfAbsent(record.key.getAsTablePath(), id -> new StringBuilder()).append(consolePost).append("\n");
        }


        /*if (report.length() > 0) {
            if (e == null) {
                e = Shuffleboard.getTab("mattlib").add("console", report.toString()).getEntry();
            }
            e.setString(report.toString());
        }*/




    }

    @Override
    public void simulationPeriodic() {
    }



}
