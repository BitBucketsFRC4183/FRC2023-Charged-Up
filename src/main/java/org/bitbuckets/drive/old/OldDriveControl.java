package org.bitbuckets.drive.old;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.bitbuckets.drive.DriveConstants;
import org.bitbuckets.drive.module.DriveModule;
import org.bitbuckets.lib.log.DataLogger;

/**
 * Represents a real drive controller that implements control of the drivetrain using a list of SwerveModule interfaces
 */
public class OldDriveControl {

    final DataLogger<OldDriveDataAutoGen> logger;
    final DriveModule[] driveModules;

    public OldDriveControl(DataLogger<OldDriveDataAutoGen> logger, DriveModule... driveModules) {
        this.logger = logger;
        this.driveModules = driveModules;
    }

    SwerveModuleState[] cachedSetpoint = DriveConstants.LOCK;

    void guaranteedLoggingLoop() {
        logger.process(data -> {
            data.targetStates = reportSetpointStates();
            data.realStates = reportActualStates();
        });
    }

    public SwerveModuleState[] reportSetpointStates() {
        return cachedSetpoint;
    }

    public SwerveModuleState[] reportActualStates() {
        SwerveModuleState[] states = new SwerveModuleState[4];

        for (int i = 0; i < driveModules.length; i++) {
            states[i] = driveModules[i].reportState();
        }

        return states;
    }

    public SwerveModulePosition[] reportActualPositions() {
        SwerveModulePosition[] states = new SwerveModulePosition[4];

        for (int i = 0; i < driveModules.length; i++) {
            states[i] = driveModules[i].reportPosition();
        }

        return states;
    }

    public void doDriveWithStates(SwerveModuleState[] states) {

        System.out.println(states[0].speedMetersPerSecond);


        for (int i = 0; i < states.length; i++) {
            driveModules[i].commandSetpointValues(states[i].speedMetersPerSecond, states[i].angle.getRadians());
        }

        cachedSetpoint = states;
    }


}