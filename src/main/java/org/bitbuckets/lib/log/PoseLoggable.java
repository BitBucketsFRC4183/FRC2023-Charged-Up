package org.bitbuckets.lib.log;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.GenericEntry;

import java.util.concurrent.CompletableFuture;

public class PoseLoggable implements ILoggable<Pose2d> {

    final CompletableFuture<GenericEntry> ftr;

    public PoseLoggable(CompletableFuture<GenericEntry> ftr) {
        this.ftr = ftr;
    }

    @Override
    public void log(Pose2d in) {
        double[] data = new double[3];
        data[0] = in.getX();
        data[1] = in.getY();
        data[2] = in.getRotation().getRadians();

        if (ftr.isDone()) {
            ftr.join().setValue(data);
        } else {
            ftr.thenAccept(e->e.setValue(data));
        }
    }
}
