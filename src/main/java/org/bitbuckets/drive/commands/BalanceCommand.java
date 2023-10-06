package org.bitbuckets.drive.commands;

import config.Drive;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.control.IPIDCalculator;
import org.bitbuckets.lib.log.IDebuggable;
import org.bitbuckets.odometry.IOdometryControl;

public class BalanceCommand extends CommandBase {

    final IOdometryControl odometryControl;
    final IDriveControl driveControl;
    final IDebuggable debuggable;

    final IPIDCalculator balanceOutput;
    final IPIDCalculator waitTimeResponse;

    int dumbCounter = 0;
    boolean shouldStop = false;

    public BalanceCommand(IOdometryControl odometryControl, IDriveControl driveControl, IDebuggable debuggable, IPIDCalculator balanceOutput, IPIDCalculator waitTimeResponse) {
        this.odometryControl = odometryControl;
        this.driveControl = driveControl;
        this.debuggable = debuggable;
        this.balanceOutput = balanceOutput;
        this.waitTimeResponse = waitTimeResponse;

        addRequirements(driveControl);
    }

    @Override
    public void execute() {
        double pitch_deg = odometryControl.getPitch_deg();

        debuggable.log("pitch-now", pitch_deg);
        debuggable.log("accel", odometryControl.getAccelerationZ());
        if (Math.abs(pitch_deg) > 2) {
            if (shouldStop) {
                double waitFB = waitTimeResponse.calculateNext(odometryControl.getYaw_deg(), 0);
                double adjustedTime = 40 + waitFB;

                debuggable.log("fb-alone", waitFB);
                debuggable.log("fb-adjusted-time", adjustedTime);

                if (dumbCounter > 50) {
                    dumbCounter = 0;
                    driveControl.drive(new ChassisSpeeds(0, 0, 0));

                } else {
                    dumbCounter++;
                }
                return;
            }

            //phase 0
            if (odometryControl.getAccelerationZ() > Drive.ACCEL_THRESHOLD_AUTOBALANCE) {
                shouldStop = true;
                driveControl.drive(new ChassisSpeeds(Math.signum(pitch_deg) * 0.7, 0, 0));
            } else {
                double output = balanceOutput.calculateNext(pitch_deg, 0);
                driveControl.drive(new ChassisSpeeds(-output, 0.0, 0.0));
            }


        } else { //deadband

            debuggable.log("is-running-ab-2", false);
            driveControl.stop();

        }
    }


    @Override
    public boolean isFinished() {
        return shouldStop && dumbCounter == 0;
    }

    @Override
    public void end(boolean interrupted) {
        driveControl.stop();
    }
}
