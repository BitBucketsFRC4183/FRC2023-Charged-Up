package org.bitbuckets.vision;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N5;
import edu.wpi.first.math.numbers.N7;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import org.opencv.core.Mat;

public interface VisionConstants2 {

    double TAG_HEIGHT = Units.inchesToMeters(24.63);
    double CAMERA_HEIGHT = Units.inchesToMeters(11.5);
    double CAMERA_PITCH = Units.degreesToRadians(9);

    TrapezoidProfile.Constraints X_CONSTRAINTS = new TrapezoidProfile.Constraints(1, .75);
    TrapezoidProfile.Constraints Y_CONSTRAINTS = new TrapezoidProfile.Constraints(1, .75);
    TrapezoidProfile.Constraints OMEGA_CONSTRAINTS = new TrapezoidProfile.Constraints(4, 4);



    int TAG_TO_CHASE = 2;

    Transform3d TAG_TO_GOAL =
            new Transform3d(
                    new Translation3d(0.2, 0, 0.0),
                    new Rotation3d(0.0, 0, Math.PI));





}
