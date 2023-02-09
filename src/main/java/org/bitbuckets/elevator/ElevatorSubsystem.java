package org.bitbuckets.elevator;

import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.log.Debuggable;

public class ElevatorSubsystem {
    final ElevatorControl elevatorControl;
    final ElevatorInput elevatorInput;
    final Debuggable debug;
    final AutoSubsystem autoSubsystem;


    public ElevatorSubsystem(ElevatorControl elevatorControl, ElevatorInput elevatorInput, Debuggable debug, AutoSubsystem autoSubsystem) {
        this.elevatorControl = elevatorControl;
        this.elevatorInput = elevatorInput;
        this.debug = debug;
        this.autoSubsystem = autoSubsystem;
    }


    public void teleopPeriodic() {

/*
(X) Tap Face Button to switch to store position
(Y) Tap Face Button to switch to high position
(A) Tap face button to switch to middle position
 */

        //  elevatorControl.setElevatorMech2d();
        if (elevatorInput.getInputB()) {
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

        } else if (elevatorInput.getInputX()) {
            // default

            elevatorControl.gotoPositionButton();
        } else if (elevatorInput.getInputB()) {
            //loading zone

            elevatorControl.gotoPositionButton();
        } else if (elevatorInput.getInputY()) {
            //high node

            elevatorControl.gotoPositionButton();
        } else if (elevatorInput.getInputA()) {
            // midle node

            elevatorControl.gotoPositionButton();
        } else {
            elevatorControl.stopExtend();

        }


    }
}
