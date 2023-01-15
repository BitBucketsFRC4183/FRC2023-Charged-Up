package frc.robot.commands;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.config.Config;
import frc.robot.config.PID;
import frc.robot.subsystem.DrivetrainSubsystem;

import java.util.function.DoubleSupplier;

public class AutoBalanceCommand extends CommandBase {



    private final DrivetrainSubsystem drivetrainSubsystem;
    PID pid = new PID();
    public PIDController balanceController = new PIDController(pid.getKP(), pid.getKI(), pid.getKD());
    SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0,0, 0);
    double speed = -0.4;


    public AutoBalanceCommand(DrivetrainSubsystem drivetrainSubsystem) {
        this.drivetrainSubsystem =  drivetrainSubsystem;
        addRequirements(drivetrainSubsystem);


    }


    @Override
    public void execute() {

        double acceleration = drivetrainSubsystem.gyro.getRoll() * -9.8;

        if (drivetrainSubsystem.gyro.getRoll() > 6) {
            drivetrainSubsystem.driveForward();





        }
        else if (drivetrainSubsystem.gyro.getRoll() < -15) {
            drivetrainSubsystem.driveBack();


        }
        else {
            drivetrainSubsystem.stop();

        }
    }
}
