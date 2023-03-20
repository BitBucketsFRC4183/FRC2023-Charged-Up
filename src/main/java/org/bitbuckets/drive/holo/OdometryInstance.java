package org.bitbuckets.drive.holo;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.drive.controlsds.DriveControl;

//TODO ASSERTION: initialization relative IS field relative because we said so. Fuse gyro onto this later.
public class OdometryInstance {

    final SwerveDriveKinematics kinematics;
    final IDriveControl driveControl;
    final Pose2d initialPose_initializationRelative;
    final SwerveModulePosition[] initialPositions_initializationRelative;

    public OdometryInstance(SwerveDriveKinematics kinematics, IDriveControl driveControl, Pose2d initialPose_initializationRelative, SwerveModulePosition[] initialPositions_initializationRelative) {
        this.kinematics = kinematics;
        this.driveControl = driveControl;
        this.initialPose_initializationRelative = initialPose_initializationRelative;
        this.initialPositions_initializationRelative = initialPositions_initializationRelative;

        lastPose_initializationRelative = initialPose_initializationRelative;
        lastPositions_initializationRelative = initialPositions_initializationRelative;
    }

    SwerveModulePosition[] lastPositions_initializationRelative;
    Pose2d lastPose_initializationRelative;

    //TODO shitty hack
    double lastAngle = 0;

    //technically this should be fine because initialization relative is guarunteed to be field relative now
    public Pose2d estimateInitializationRelativePose() {
        SwerveModulePosition[] currentPositions_initializationRelative = driveControl.currentPositions_initializationRelative();
        SwerveModulePosition[] delta_initializationRelative = new SwerveModulePosition[currentPositions_initializationRelative.length];

        for (int i = 0; i < currentPositions_initializationRelative.length; i++) {
            delta_initializationRelative[i] = new SwerveModulePosition(
                    currentPositions_initializationRelative[i].distanceMeters - lastPositions_initializationRelative[i].distanceMeters,
                    currentPositions_initializationRelative[i].angle
            );
        }

        Twist2d deltaTwist_initializationRelative = kinematics.toTwist2d(delta_initializationRelative);
        Rotation2d deltaRotationFromTwist = new Rotation2d(deltaTwist_initializationRelative.dtheta);

        Pose2d nextPosition_initializationRelative = lastPose_initializationRelative.exp(deltaTwist_initializationRelative);

        //update old
        lastPose_initializationRelative = nextPosition_initializationRelative;
        lastPositions_initializationRelative = currentPositions_initializationRelative;

        return nextPosition_initializationRelative;
    }
}
