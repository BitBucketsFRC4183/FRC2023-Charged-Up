package org.bitbuckets.drive.controlsds;

import org.bitbuckets.drive.DriveSubsystem;

public class LogDriveSubsystem implements Runnable {

    final DriveSubsystem driveSubsystem;

    public LogDriveSubsystem(DriveSubsystem driveSubsystem) {
        this.driveSubsystem = driveSubsystem;
    }

    @Override
    public void run() {

    }
}
