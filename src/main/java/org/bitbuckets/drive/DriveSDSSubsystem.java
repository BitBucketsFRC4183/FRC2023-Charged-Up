package org.bitbuckets.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.bitbuckets.drive.control.DriveControl;
import org.bitbuckets.drive.controlsds.DriveControlSDS;
import org.bitbuckets.lib.util.MathUtil;


/**
 * subsystemize!
 */
public class DriveSDSSubsystem {

    final DriveInput input;
    final DriveControlSDS control;

    public DriveSDSSubsystem(DriveInput input, DriveControlSDS control) {
        this.input = input;
        this.control = control;
    }

    DriveFSM state = DriveFSM.TELEOP_NORMAL;

    //Needs to stop if we're going fw or bw
    public void teleopPeriodic() {

        switch (state) {
            case UNINITIALIZED:
                //do nothing
                break;
            case TELEOP_NORMAL:
                if (input.isAutoBalancePressed()) {
                    state = DriveFSM.TELEOP_BALANCING; //do balancing next iteration
                    break;
                }
                if(input.isAutoHeadingPressed())
                {
                    state = DriveFSM.TELEOP_AUTOHEADING;
                    break;
                }
                
                double xOutput = input.getInputX() * control.getMaxVelocity();
                double yOutput = input.getInputY() * control.getMaxVelocity();
                double rotationOutput = input.getInputRot() * control.getMaxAngularVelocity();

                if (xOutput == 0 && yOutput == 0 && rotationOutput == 0) {
                    control.stopSticky();
                } else {
                    ChassisSpeeds desired = new ChassisSpeeds(xOutput, yOutput, rotationOutput);
                    control.drive(desired);
                }

                //check the buttons to make sure we dont want a state transition
                break;
            case TELEOP_BALANCING:
                if(input.isDefaultPressed())
                {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                double BalanceDeadband_deg = Preferences.getDouble(DriveSDSConstants.autoBalanceDeadbandDegKey, DriveSDSConstants.BalanceDeadbandDeg);

                double Roll_deg = control.getRoll_deg();
                if (Math.abs(Roll_deg) > BalanceDeadband_deg) {
                    double output = control.calculateBalanceOutput(Roll_deg, 0);
                    control.drive(new ChassisSpeeds(output, 0.0, 0.0));
                }
                else {
                    control.stopSticky();

                }
                break;
                //DO teleop balancing here
            case TELEOP_AUTOHEADING:
                if(input.isDefaultPressed())
                {
                    state = DriveFSM.TELEOP_NORMAL;
                    break;
                }
                double IMU_Yaw = Math.toRadians(control.getYaw_deg());//Math.toRadians(-350);

                IMU_Yaw = MathUtil.wrap(IMU_Yaw);

                //will add logic later
                double setpoint = Math.toRadians(90);

                double error = setpoint-IMU_Yaw;

                SmartDashboard.putNumber("AutoOrient_setpoint",Math.toDegrees(setpoint));
                SmartDashboard.putNumber("AutoOrient_wrappedYaw",Math.toDegrees(IMU_Yaw));
                SmartDashboard.putNumber("AutoOrient_Error",Math.toDegrees(error));

                double rotationOutputOrient = control.calculateRotOutputRad(
                        IMU_Yaw,
                        setpoint
                );
//        if(Math.abs(error) < 180)
//        {
//            rotationOutput = -rotationOutput;
//        }
                if(Math.abs(error) > Math.toRadians(2))
                {
                    control.drive(
                            new ChassisSpeeds(0, 0, rotationOutputOrient)
                    );
                }
                else
                {
                    control.stop();
                }




                break;

        }
    }
}
