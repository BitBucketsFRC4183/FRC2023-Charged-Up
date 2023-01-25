package org.bitbuckets.elevator;

public class ElevatorSubsystem {
    final ElevatorControl elevatorControl;
    final ElevatorInput elevatorInput;

    public ElevatorSubsystem(ElevatorControl elevatorControl, ElevatorInput elevatorInput) {
        this.elevatorControl = elevatorControl;
        this.elevatorInput = elevatorInput;
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
            elevatorControl.tiltLeft();
        }
        if(elevatorInput.getInputDpadRight())
        {
            elevatorControl.tiltRight();
        }
    }
}
