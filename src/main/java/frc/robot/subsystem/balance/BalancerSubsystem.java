package frc.robot.subsystem.balance;


import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.robot.config.PID;

public class BalancerSubsystem {
    WPI_PigeonIMU gyro = new WPI_PigeonIMU(5);




    PID pid = new PID();
    Drive drive = new Drive();
    public PIDController balanceController = new PIDController(pid.getKP(), pid.getKI(), pid.getKD());
    SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0,0, 0);
    double speed = -0.4;
    }


        public void Balancing() {
        double acceleration = gyro.getRoll() * -9.8;


            gyro.getAngle();
            if (gyro.getRoll() > 2) {

                drive.bottomLeft.set(-speed);
                drive.bottomRight.set(speed);
                drive.topRight.set(speed);
                drive.topLeft.set(-speed);





                feedforward.calculate(-speed, acceleration);


                balanceController.setPID(.1, 0, 0);






            }
            else if (gyro.getRoll() < -2) {
                drive.bottomLeft.set(speed);
                drive.bottomRight.set(-speed);
                drive.topRight.set(-speed);
                drive.topLeft.set(speed);

                balanceController.setPID(.1, 0, 0);

            }
            else {
                drive.bottomLeft.set(0);
                drive.bottomRight.set(0);
                drive.topRight.set(0);
                drive.topLeft.set(0);
            }
        }
        public double getAngle()
        {
            return gyro.getRoll();
        }


    }




