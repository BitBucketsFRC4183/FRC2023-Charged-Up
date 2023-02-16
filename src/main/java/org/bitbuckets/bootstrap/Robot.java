package org.bitbuckets.bootstrap;

import com.revrobotics.REVPhysicsSim;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.TimedRobot;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.IConsole;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.log.LogRecord;
import org.bitbuckets.lib.log.Process;
import org.bitbuckets.lib.log.ProcessConsole;

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
    IConsole builtRootConsole;

    public Robot(ISetup<Void> buildRobot) {
        this.buildRobot = buildRobot;
    }

    @Override
    public void robotInit() {
        try {
            builtProcess = Process.root();
            buildRobot.build(builtProcess);
        } catch (Exception e) {
            DriverStation.reportError(e.getLocalizedMessage(), e.getStackTrace());
        }

    }

    NetworkTable rootTable;

    @Override
    public void robotPeriodic() {
        Threads.setCurrentThreadPriority(true, 99); //stupid hack
        builtProcess.run();

        StringBuilder report = new StringBuilder();
        Map<String, StringBuilder> thisIsBad = new HashMap<>();

        while (!ProcessConsole.QUEUE.isEmpty()) {
            LogRecord record = ProcessConsole.QUEUE.remove();

            String consolePost;
            if (record.exception == null) {
                consolePost = format(
                        "[%s]: %s",
                        record.key.getAsLastTwoPath(),
                        record.info
                );
            } else {
                consolePost = format(
                        "[%s] ERROR: %s (%s:%s)",
                        record.key.getAsLastTwoPath(),
                        record.exception.getLocalizedMessage(),
                        record.exception.getStackTrace()[0].getMethodName(),
                        record.exception.getStackTrace()[0].getLineNumber()
                );

            }

            report.append(consolePost).append("\n");
            thisIsBad.computeIfAbsent(record.key.getAsTablePath(), id -> new StringBuilder()).append(consolePost).append("\n");
        }


        if (report.length() > 0) {
            rootTable.getEntry("mattlib/console").setString(report.toString());
        }

        thisIsBad.forEach((a,b) -> {
            if (b.length() > 0) {
                rootTable.getEntry("mattlib/" + a).setString(b.toString());
            }
        });




    }

    @Override
    public void simulationPeriodic() {
        REVPhysicsSim.getInstance().run();
    }


}
