package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.utils.Xbox;

public class Buttons {

  //////////////////////////////////////////////////////////////////////////////
  // Driver
  Joystick driverControl = new Joystick(0);
  int swerveForward = Xbox.LEFT_STICK_Y;
  int swerveStrafe = Xbox.LEFT_STICK_X;
  int swerveRotation = Xbox.RIGHT_STICK_X;

  JoystickButton resetOdometry = new JoystickButton(driverControl, Xbox.OPTIONS);
  int lt = 2;
  int rt = 3;
  Button slowDrive = new Button(() -> driverControl.getRawAxis(lt) > 0.1);
  JoystickButton autoBalance = new JoystickButton(driverControl, XboxController.Button.kB.value);

  //use A go to 90 degrees
  //  ---- <- upper arm (horizontal)
  //     |
  //     | <- lower arm (vertical)
  // don't question this

  JoystickButton move90 = new JoystickButton(driverControl, XboxController.Button.kA.value);

  //use X to go to 6 degrees (note that 0 wouldn't be goood because of interference
  JoystickButton move6 = new JoystickButton(driverControl, XboxController.Button.kX.value);

  //upper joint goes backward
  JoystickButton upperBackwards = new JoystickButton(driverControl, XboxController.Button.kRightBumper.value);

  //lower joint goes backward
  JoystickButton lowerBackwards = new JoystickButton(driverControl, XboxController.Button.kLeftBumper.value);

  //toggles between auto and manual, start button is tiny right black one with 3 lines
  JoystickButton toggleAutoManual = new JoystickButton(driverControl, XboxController.Button.kStart.value);
;
  //Operator
  Joystick operatorControl = new Joystick(1);
}
