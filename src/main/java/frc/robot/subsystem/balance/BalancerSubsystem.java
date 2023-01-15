package frc.robot.subsystem.balance;


import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.config.Config;
import frc.robot.config.PID;
import frc.robot.subsystem.BitBucketsSubsystem;
import frc.robot.subsystem.DrivetrainSubsystem;

public class BalancerSubsystem extends BitBucketsSubsystem {
    WPI_PigeonIMU gyro = new WPI_PigeonIMU(5);

    private Config config;

    private final DrivetrainSubsystem drivetrainSubsystem;


    PID pid = new PID();
    public PIDController balanceController = new PIDController(pid.getKP(), pid.getKI(), pid.getKD());
    SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0,0, 0);
    double speed = -0.4;

    public BalancerSubsystem(Config config, DrivetrainSubsystem drivetrainSubsystem) {
        super(config);
        this.drivetrainSubsystem =  drivetrainSubsystem;

    }

    @Override
    public void init() {

    }

    @Override
    public void periodic() {

    }

    @Override
    public void disable() {

    }


    public void Balancing() {
        double acceleration = gyro.getRoll() * -9.8;


            if (gyro.getRoll() > 3) {
                drivetrainSubsystem.driveForward();



               // feedforward.calculate(-speed, acceleration);


              //  balanceController.setPID(.1, 0, 0);



            }
            else if (gyro.getRoll() < -3) {
                drivetrainSubsystem.driveBack();

            //    balanceController.setPID(.1, 0, 0);

            }
            else {
                drivetrainSubsystem.stop();

            }
        }
        public double getAngle()
        {
            return gyro.getRoll();
        }


    }




