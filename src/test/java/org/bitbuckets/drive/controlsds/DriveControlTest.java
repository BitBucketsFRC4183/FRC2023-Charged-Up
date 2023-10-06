package org.bitbuckets.drive.controlsds;

import config.DriveTurdSpecific;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import org.bitbuckets.drive.DriveControl;
import org.bitbuckets.drive.ISwerveModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DriveControlTest {

    ISwerveModule moduleFrontLeft;
    ISwerveModule moduleFrontRight;
    ISwerveModule moduleBackLeft;
    ISwerveModule moduleBackRight;

    DriveControl control;

    @BeforeEach
    void setUp() {
        moduleFrontLeft = mock();
        moduleFrontRight = mock();
        moduleBackLeft = mock();
        moduleBackRight = mock();
        control = new DriveControl(
                DriveTurdSpecific.KINEMATICS,
                mock(),
                moduleFrontLeft,
                moduleFrontRight,
                moduleBackLeft,
                moduleBackRight
        );
    }

    @Test
    void driveForward() {
        // drive 1 m/s forward (away from alliance wall)
        control.drive(new ChassisSpeeds(1, 0, 0));

        // this gives each swerve module positive voltage
        verify(moduleFrontLeft).set(AdditionalMatchers.eq(3d, .1), eq(0d));
        verify(moduleFrontRight).set(AdditionalMatchers.eq(3d, .1), eq(0d));
        verify(moduleBackLeft).set(AdditionalMatchers.eq(3d, .1), eq(0d));
        verify(moduleBackRight).set(AdditionalMatchers.eq(3d, .1), eq(0d));
    }

    @Test
    void driveBackward() {
        // drive 1 m/s backwards (toward alliance wall)
        control.drive(new ChassisSpeeds(-1, 0, 0));

        // this gives each swerve module positive voltage, but rotated 180º so this actually moves the robot backwards
        verify(moduleFrontLeft).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI));
        verify(moduleFrontRight).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI));
        verify(moduleBackLeft).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI));
        verify(moduleBackRight).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI));
    }

    @Test
    void driveLeft() {
        // drive 1 m/s "left" (positive along y axis)
        control.drive(new ChassisSpeeds(0, 1, 0));

        // this gives each swerve module positive voltage, but rotated 90º so this actually moves the robot backwards
        verify(moduleFrontLeft).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI / 2));
        verify(moduleFrontRight).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI / 2));
        verify(moduleBackLeft).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI / 2));
        verify(moduleBackRight).set(AdditionalMatchers.eq(3d, .1), eq(Math.PI / 2));
    }

    @Test
    void driveRight() {
        // drive 1 m/s "right" (negative along y axis)
        control.drive(new ChassisSpeeds(0, -1, 0));

        // this gives each swerve module positive voltage, but rotated 90º so this actually moves the robot backwards
        verify(moduleFrontLeft).set(AdditionalMatchers.eq(3d, .1), eq(-Math.PI / 2));
        verify(moduleFrontRight).set(AdditionalMatchers.eq(3d, .1), eq(-Math.PI / 2));
        verify(moduleBackLeft).set(AdditionalMatchers.eq(3d, .1), eq(-Math.PI / 2));
        verify(moduleBackRight).set(AdditionalMatchers.eq(3d, .1), eq(-Math.PI / 2));
    }
}