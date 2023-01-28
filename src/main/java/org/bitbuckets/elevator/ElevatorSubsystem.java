package org.bitbuckets.elevator;

public class ElevatorSubsystem {
    final ElevatorControl elevatorControl;
    final ElevatorInput elevatorInput;

    public ElevatorSubsystem(ElevatorControl elevatorControl, ElevatorInput elevatorInput) {
        this.elevatorControl = elevatorControl;
        this.elevatorInput = elevatorInput;
    }

    public void robotPeriodic()
    {
        elevatorControl.setElevatorMech2d();
    }


    public void teleopPeriodic(){

        if(elevatorInput.getInputDpadUp())
        {
            elevatorControl.extendUp();
        }
        if(elevatorInput.getInputDpadUp())
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
