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

    public void robotPeriodic() {
        debug.log("we reached this NO AUTO", "HERE");

        if (autoSubsystem.sampleHasEventStarted("testhehe")) {
            debug.log("we reached this", "HERE");
        }
    }


    public void teleopPeriodic() {


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


        } else if (elevatorInput.getInputA()) {

            elevatorControl.gotoPositionButton();


        } else {
            elevatorControl.stopExtend();

        }

        elevatorControl.smartDashboard();


    }
}
