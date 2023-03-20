package org.bitbuckets.drive.holo;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IGyro;

public class WorseOdometryControlSetup implements ISetup<WorseOdometryControl> {

    final SwerveDriveKinematics kinematics;
    final IDriveControl driveControl;
    final ISetup<IGyro> gyro;

    public WorseOdometryControlSetup(SwerveDriveKinematics kinematics, IDriveControl driveControl, ISetup<IGyro> gyro) {
        this.kinematics = kinematics;
        this.driveControl = driveControl;
        this.gyro = gyro;
    }

    @Override
    public WorseOdometryControl build(IProcess self) {
        return new WorseOdometryControl(
                kinematics,
                driveControl,
                self.childSetup("gyro", gyro),
                new OdometryInstance(
                        kinematics,
                        driveControl,
                        new Pose2d(), //If auto doesnt set anything, assume we start at 0,0,0deg
                        driveControl.currentPositions_initializationRelative()
                )
        );
    }
}
