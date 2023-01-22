package org.bitbuckets.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.drive.DriveSubsystem;

/**
 * This class represents your robot's periodic behavior
 */
public class RobotContainer {

    final DriveSubsystem subsystem;

    public RobotContainer(DriveSubsystem subsystem) {
        this.subsystem = subsystem;
    }

    public void autoPeriodic() {



    }

    //Shouldn't need to do anything here
    public void teleopPeriodic() {


        TalonFX d    = new TalonFX(0);

        d.set(TalonFXControlMode.Position, 0);


        subsystem.teleopPeriodic();
    }

}
