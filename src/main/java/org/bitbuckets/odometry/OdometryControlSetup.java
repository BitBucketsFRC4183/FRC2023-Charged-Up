package org.bitbuckets.odometry;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.bootstrap.Robot;

import static org.bitbuckets.drive.controlsds.DriveControlSDS.gyro;

public class OdometryControlSetup {
    if (Robot.isSimulation()) {
        RobotSim.getInstance().addTalonSRX(topleft, .75, 5100, false);
        RobotSim.getInstance().addTalonSRX(topright, .75, 5100, false);
    }

    {
        gyro.reset();
        SmartDashboard.putData(OdometryControl.field);
    }
}
