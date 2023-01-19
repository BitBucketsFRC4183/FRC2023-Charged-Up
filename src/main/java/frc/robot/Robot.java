// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.sensors.WPI_PigeonIMU;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.AutoBalanceCommand;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.config.Config;
import frc.robot.simulator.CTREPhysicsSim;
import frc.robot.simulator.SetModeTestSubsystem;
import frc.robot.simulator.SimulatorTestSubsystem;
import frc.robot.subsystem.*;
import frc.robot.subsystem.balance.BalancerSubsystem;
import frc.robot.utils.MathUtils;

import java.util.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {

  //private final Loggable<String> info = BucketLog.loggable(Put.STRING, "general/info");

  private Buttons buttons;
  private Config config;

  private boolean isBalancing = !false;

  //start robot with manual control
  private boolean manualMode = true;
  WPI_PigeonIMU gyro = new WPI_PigeonIMU(5);

  private final List<BitBucketsSubsystem> robotSubsystems = new ArrayList<>();

  //Subsystems
  private DrivetrainSubsystem drivetrainSubsystem;
  private ArmSubsystem armSubsystem;

  private Field2d field;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    this.config = new Config();
    this.buttons = new Buttons();
    this.field = new Field2d();

    LiveWindow.disableAllTelemetry();


    if (config.enableDriveSubsystem) {
      this.robotSubsystems.add(drivetrainSubsystem = new DrivetrainSubsystem(this.config));
    }

    // create a new field to update
    SmartDashboard.putData("Field", field);

    // Configure the button bindings
    this.configureButtonBindings();

    // Subsystem Initialize Loop
    if (System.getenv().containsKey("CI")) {
      //this.robotSubsystems.add(new LogTestSubsystem(this.config));
      this.robotSubsystems.add(new SimulatorTestSubsystem(this.config));
    }

    this.robotSubsystems.add(new SetModeTestSubsystem(this.config));

    // Subsystem Initialize Loop

    this.robotSubsystems.forEach(BitBucketsSubsystem::init);

    //Create the Autonomous Commands now so we don't do this every time autonomousInit() gets called
    if(config.enableAutonomousSubsystem){
    }
  }

  boolean wasShooting;


  @Override
  public void robotPeriodic() {

    CommandScheduler.getInstance().run();
    SmartDashboard.putNumber("gyro",drivetrainSubsystem.gyro.getRoll());
    //this.robotSubsystems.forEach(BitBucketsSubsystem::periodic);
  }


  @Override
  public void autonomousInit() {

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {

    if (config.enableDriveSubsystem) {
//
      drivetrainSubsystem.setDefaultCommand(
              new AutoBalanceCommand(
                      drivetrainSubsystem

              )
      );

    }



  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    //booleans for state of robot's joints
    boolean manual;
    boolean upperForward;
    boolean lowerForward;

    drivetrainSubsystem.stop();

    if (manualMode) {
      // trigger is between 0 and 1, where 1 is fully pressed
      double lowerJointOutput = buttons.driverControl.getRawAxis(XboxController.Axis.kLeftTrigger.value);
      double upperJointOutput = buttons.driverControl.getRawAxis(XboxController.Axis.kRightTrigger.value);

      if (lowerJointOutput > 0.01){
        armSubsystem.moveLowerArm(lowerJointOutput);
      }
      if(upperJointOutput > 0.01){
        armSubsystem.moveUpperArm(lowerJointOutput);
      }
    }
    else{

    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    this.robotSubsystems.forEach(BitBucketsSubsystem::disable);
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    if(config.enableDriveSubsystem){
      this.drivetrainSubsystem.stop();
    }
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}


  public void toggleBalance()
  {
    CommandScheduler.getInstance().cancelAll();
    isBalancing = !isBalancing;
    if(isBalancing)
    {
      drivetrainSubsystem.setDefaultCommand(
              new AutoBalanceCommand(
                      drivetrainSubsystem

              )
      );
    }
    else
    {
      drivetrainSubsystem.setDefaultCommand(
              new DefaultDriveCommand(
                      drivetrainSubsystem,
                      () -> -MathUtils.modifyAxis(buttons.driverControl.getRawAxis(buttons.swerveForward)),
                      () -> -MathUtils.modifyAxis(buttons.driverControl.getRawAxis(buttons.swerveStrafe)),
                      () -> -MathUtils.modifyAxis(buttons.driverControl.getRawAxis(buttons.swerveRotation))
              )
      );
    }
  }

  public void toggleManual(){

    //toggle manual to auto or vice versa
    manualMode = !manualMode;
  }


  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {

  }

  @Override
  public void simulationPeriodic() {
    CTREPhysicsSim.getInstance().run();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by
   * instantiating a {GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {XboxController}), and then passing it to
   * a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Back button zeros the gyroscope
    if (config.enableDriveSubsystem) {
      buttons.resetOdometry.whenPressed(
        () -> {
          this.drivetrainSubsystem.setOdometry(new Pose2d(0, 0, new Rotation2d(0)));
          this.drivetrainSubsystem.zeroGyro();
        }
      );


      buttons.slowDrive.whenPressed(() -> this.drivetrainSubsystem.speedModifier = 0.25);
      buttons.slowDrive.whenReleased(() -> this.drivetrainSubsystem.speedModifier = 0.75);


    buttons.autoBalance.whenPressed(() -> toggleBalance());

    //when start button is pressed, call toggleManual function
    buttons.toggleAutoManual.whenPressed(() -> toggleManual());



    }

  }
}
