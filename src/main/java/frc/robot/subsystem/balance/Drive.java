package frc.robot.subsystem.balance;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class Drive {


        WPI_TalonSRX topRight = new WPI_TalonSRX(4);
        WPI_TalonSRX bottomRight = new WPI_TalonSRX(1);


        MotorControllerGroup right = new MotorControllerGroup(topRight, bottomRight);

        WPI_TalonSRX topLeft = new WPI_TalonSRX(3);
        WPI_TalonSRX bottomLeft = new WPI_TalonSRX(2);

        MotorControllerGroup left = new MotorControllerGroup(topLeft, bottomLeft);


        DifferentialDrive drive = new DifferentialDrive(left, right);


        public void init() {
            topRight.setInverted(true);
            bottomRight.setInverted(true);
            //playSound();
        }


        public void teleopPeriodic() {

        }


        public void setDesiredMotorState(double forwardBack, double leftRight) {
            drive.arcadeDrive(forwardBack, leftRight);
        }


    }

