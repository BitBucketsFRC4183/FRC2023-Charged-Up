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
        elevatorControl.setElevatorMech2d();
        if (elevatorInput.getInputSquare()) {
            elevatorControl.zeroTilt();
            elevatorControl.zeroExtend();
        }
        switch (state) {
            case off:
                if (elevatorInput.getInputDpadUp()) {
                    state = ElevatorFSM.up;
                    break;

                }
                if (elevatorInput.getInputDpadDown()) {
                    state = ElevatorFSM.down;
                    break;

                }
                if (elevatorInput.getInputDpadLeft()) {
                    state = ElevatorFSM.back;
                    break;

                }
                if (elevatorInput.getInputDpadRight()) {
                    state = ElevatorFSM.forward;
                    break;

                }
                if (elevatorInput.getInputCirlce()) {
                    state = ElevatorFSM.automatic;
                    break;
                }
                break;
            case up:
                if (elevatorInput.getInputDpadDown()) {
                    state = ElevatorFSM.down;
                    break;

                }
                if (elevatorInput.getInputDpadLeft()) {
                    state = ElevatorFSM.back;
                    break;

                }
                if (elevatorInput.getInputDpadRight()) {
                    state = ElevatorFSM.forward;
                    break;

                }
                if (elevatorInput.getInputCirlce()) {
                    state = ElevatorFSM.automatic;
                    break;
                }
                elevatorControl.extendUp();
                break;
            case down:
                if (elevatorInput.getInputDpadUp()) {
                    state = ElevatorFSM.up;
                    break;

                }
                if (elevatorInput.getInputDpadLeft()) {
                    state = ElevatorFSM.back;
                    break;

                }
                if (elevatorInput.getInputDpadRight()) {
                    state = ElevatorFSM.forward;
                    break;

                }
                if (elevatorInput.getInputCirlce()) {
                    state = ElevatorFSM.automatic;
                    break;
                }
                elevatorControl.extendDown();
                break;
            case forward:
                if (elevatorInput.getInputDpadUp()) {
                    state = ElevatorFSM.up;
                    break;

                }
                if (elevatorInput.getInputDpadDown()) {
                    state = ElevatorFSM.down;
                    break;

                }
                if (elevatorInput.getInputDpadLeft()) {
                    state = ElevatorFSM.back;
                    break;

                }
                if (elevatorInput.getInputCirlce()) {
                    state = ElevatorFSM.automatic;
                    break;
                }
                elevatorControl.tiltForward();
                break;

            case back:
                if (elevatorInput.getInputDpadUp()) {
                    state = ElevatorFSM.up;
                    break;

                }
                if (elevatorInput.getInputDpadDown()) {
                    state = ElevatorFSM.down;
                    break;

                }
                if (elevatorInput.getInputDpadRight()) {
                    state = ElevatorFSM.forward;
                    break;

                }
                if (elevatorInput.getInputCirlce()) {
                    state = ElevatorFSM.automatic;
                    break;
                }
                elevatorControl.tiltBack();
                break;
            case automatic:
                if (elevatorInput.getInputDpadUp()) {
                    state = ElevatorFSM.up;
                    break;

                }
                if (elevatorInput.getInputDpadDown()) {
                    state = ElevatorFSM.down;
                    break;

                }
                if (elevatorInput.getInputDpadLeft()) {
                    state = ElevatorFSM.back;
                    break;

                }
                if (elevatorInput.getInputDpadRight()) {
                    state = ElevatorFSM.forward;
                    break;

                }
                elevatorControl.gotoPositionButton();


                break;

        }

        elevatorControl.smartDashboard();


    }
}
