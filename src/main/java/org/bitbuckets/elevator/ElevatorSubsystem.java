package org.bitbuckets.elevator;

public class ElevatorSubsystem {
    final ElevatorControl elevatorControl;
    final ElevatorInput elevatorInput;

    public ElevatorSubsystem(ElevatorControl elevatorControl, ElevatorInput elevatorInput) {
        this.elevatorControl = elevatorControl;
        this.elevatorInput = elevatorInput;
    }

    public void robotPeriodic() {
    }

    ElevatorFSM state = ElevatorFSM.off;

    public void teleopPeriodic() {
        //  elevatorControl.setElevatorMech2d();
        if (elevatorInput.getInputSquare()) {
            elevatorControl.zeroTilt();
            elevatorControl.zeroExtend();
        }


        if (elevatorInput.getInputDpadUp()) {
            elevatorControl.extendUp();

        } else if (elevatorInput.getInputDpadDown()) {
            elevatorControl.extendDown();

        } else if (elevatorInput.getInputDpadLeft()) {
            elevatorControl.tiltBack();

        } else if (elevatorInput.getInputDpadRight()) {
            elevatorControl.tiltForward();


        } else if (elevatorInput.getInputCirlce()) {
            
            elevatorControl.gotoPositionButton();


        } else {
            elevatorControl.stopExtend();

        }

        elevatorControl.smartDashboard();


    }
}
