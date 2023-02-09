package org.bitbuckets.elevator;

import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.log.Debuggable;

public class ElevatorSubsystem {
    final ElevatorControl elevatorControl;
    final ElevatorInput elevatorInput;
    final Debuggable debug;
    final AutoSubsystem autoSubsystem;
    final double storePosExtend = 0;
    final double storePosTilt = 0;
    final double loadZoneExtend = 0;
    final double loadZoneTilt = 0;
    final double lowPosExtend = 0;
    final double lowPosTilt = 0;
    final double middlePosExtend = 0;
    final double middlePosTilt = 0;
    final double highPosExtend = 0;
    final double highPosTilt = 0;


    public ElevatorSubsystem(ElevatorControl elevatorControl, ElevatorInput elevatorInput, Debuggable debug, AutoSubsystem autoSubsystem) {
        this.elevatorControl = elevatorControl;
        this.elevatorInput = elevatorInput;
        this.debug = debug;
        this.autoSubsystem = autoSubsystem;
    }


    public void robotPeriodic() {
        if (autoSubsystem.sampleHasEventStarted("testhehe")) {
            //random for testing
            debug.log("elevator with Auto", " we got here");
            elevatorControl.goToPosition(middlePosExtend, middlePosTilt);
        }

    }

    public void teleopPeriodic() {
        elevatorControl.extendUp(elevatorInput.getLJoystickY());
        elevatorControl.extendUp(elevatorInput.getRJoystickX());

        if (elevatorInput.getInputX()) {
            elevatorControl.goToPosition(storePosExtend, storePosTilt);
        } else if (elevatorInput.getInputDpadRight()) {
            elevatorControl.goToPosition(loadZoneExtend, loadZoneTilt);
        } else if (elevatorInput.getInputDpadLeft()) {
            elevatorControl.goToPosition(lowPosExtend, lowPosTilt);
        } else if (elevatorInput.getInputDpadUp()) {
            elevatorControl.goToPosition(highPosExtend, highPosTilt);
        } else if (elevatorInput.getInputDpadDown()) {
            elevatorControl.goToPosition(middlePosExtend, middlePosTilt);
        } else {
            elevatorControl.stopTilt();
            elevatorControl.stopExtend();
        }

    }
}
