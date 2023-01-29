package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ElevatorSubsystem {
    final ElevatorControl elevatorControl;
    final ElevatorInput elevatorInput;

    public ElevatorSubsystem(ElevatorControl elevatorControl, ElevatorInput elevatorInput) {
        this.elevatorControl = elevatorControl;
        this.elevatorInput = elevatorInput;
    }

    public void robotPeriodic()
    {
    }


    public void teleopPeriodic(){
        elevatorControl.smartDashboard();
        elevatorControl.setElevatorMech2d();
        if(elevatorInput.getInputCirlce())
        {
            elevatorControl.gotoPositionButton();
        }
        if(elevatorInput.getInputDpadUp())
        {
            elevatorControl.extendUp();
        }
        if(elevatorInput.getInputDpadDown())
        {
            elevatorControl.extendDown();
        }
        if(elevatorInput.getInputDpadLeft())
        {
            elevatorControl.tiltForward();
        }
        if(elevatorInput.getInputDpadRight())
        {
            elevatorControl.tiltBack();
        }
    }
}
