package org.bitbuckets.arm;

import config.Arm;
import org.bitbuckets.OperatorInput;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.core.HasLifecycle;
import org.bitbuckets.lib.core.HasLoop;
import org.bitbuckets.lib.log.IDebuggable;

public class ArmSubsystem implements HasLoop, HasLifecycle {

    final OperatorInput operatorInput;
    final ArmControl armControl;
    final AutoSubsystem autoSubsystem;
    final IDebuggable debuggable;


    public ArmSubsystem(OperatorInput operatorInput, ArmControl armControl, AutoSubsystem autoSubsystem, IDebuggable debuggable) {
        this.operatorInput = operatorInput;
        this.armControl = armControl;
        this.autoSubsystem = autoSubsystem;
        this.debuggable = debuggable;
    }

    ArmFSM shouldDoNext = ArmFSM.IDLE;

    @Override
    public void loop() {

        //handle inputs, which will calculate what the next input of the robot is
        handleLogic();

        debuggable.log("state", shouldDoNext);
    }

    @Override
    public void autonomousPeriodic() {

        if (autoSubsystem.sampleHasEventStarted("arm-stow")) {
            armControl.stopGripper();

            shouldDoNext = ArmFSM.STORE;
            return;
        }
        if (autoSubsystem.sampleHasEventStarted("arm-human-intake")) {
            shouldDoNext = ArmFSM.HUMAN_INTAKE;
            return;
        }
        if (autoSubsystem.sampleHasEventStarted("arm-ground-intake")) {
            shouldDoNext = ArmFSM.GROUND_INTAKE;
            return;
        }

        if (autoSubsystem.sampleHasEventStarted("arm-stop")) {
            System.out.println("j");
            shouldDoNext = ArmFSM.IDLE;
            return;
        }

        if (autoSubsystem.sampleHasEventStarted("gripper-open")) {
            shouldDoNext = ArmFSM.ACTUATE_GRIPPER;
            return;
        }

        if (autoSubsystem.sampleHasEventStarted("arm-scoreHigh")) {
            shouldDoNext = ArmFSM.SCORE_HIGH;
            return;
        }

        if (autoSubsystem.sampleHasEventStarted("arm-prepare")) {
            shouldDoNext = ArmFSM.PREPARE;
            return;
        }


        if (autoSubsystem.sampleHasEventStarted("arm-unstow")) {
            shouldDoNext = ArmFSM.UNSTOW;
            return;
        }


    }

    @Override
    public void teleopPeriodic() {

        if (operatorInput.intakeGripper()) {
            armControl.intakeGripperCone();

        } else if (operatorInput.outtakeGripper()) {
            armControl.outtakeGripper();
        }
        else if (operatorInput.isCube()){
            armControl.gripperHold();
        }
        else if (operatorInput.openGripper()){
            armControl.gripperOpen();
    }
        else {
            armControl.gripperLoop();
        }
        if (operatorInput.isStoragePressed()) {
            shouldDoNext = ArmFSM.STORE;
            return;
        }


        if(operatorInput.isZeroArmPressed()){
            armControl.zero();
        }
        if (operatorInput.isStopPidPressed()) {
            shouldDoNext = ArmFSM.IDLE;
            return;
        }

        if (operatorInput.isStoragePressed()) {
            shouldDoNext = ArmFSM.STORE;
            return;
        }
        if (operatorInput.isHumanIntakePressed()) {
            shouldDoNext = ArmFSM.HUMAN_INTAKE;
            return;
        }

        if (operatorInput.isScoreHighPressed()) {
            shouldDoNext = ArmFSM.SCORE_HIGH;
            return;
        }
        if (operatorInput.isScoreMidPressed()) {
            shouldDoNext = ArmFSM.SCORE_MID;
            return;
        }
        if (operatorInput.isScoreLowPressed()) {
            shouldDoNext = ArmFSM.SCORE_LOW;
            return;
        }
        if (operatorInput.isLoadPresed()) {
            shouldDoNext = ArmFSM.LOAD;
            return;
        }

        if (operatorInput.isManualModePressed()) {
            shouldDoNext = ArmFSM.MANUAL;
            return;
        }

        // only ever do manual control in teleop
        if (shouldDoNext == ArmFSM.MANUAL) {
            armControl.commandArmToPercent(
                    operatorInput.getLowerArm_PercentOutput() * 0.35,
                    operatorInput.getUpperArm_PercentOutput()
            );
        }
    }

    @Override
    public void autonomousInit() {
        shouldDoNext = ArmFSM.IDLE;
    }

    @Override
    public void teleopInit() {
        shouldDoNext = ArmFSM.IDLE;
        armControl.doNothing();
        armControl.stopGripper();
    }

    @Override
    public void disabledInit() {
        shouldDoNext = ArmFSM.IDLE;
        armControl.doNothing();
        armControl.stopGripper();
    }

    @Override
    public void disabledPeriodic() {
        shouldDoNext = ArmFSM.IDLE;
        armControl.doNothing();
        armControl.stopGripper();
    }

    //generates what the FSM should do. Will modify shouldDoNext if something has happened

    //acts on shouldDoNext and then updates it to the result state if it has managed to complete it's task
    void handleLogic() {

        if (shouldDoNext == ArmFSM.STORE) {
            armControl.commandArmToState(
                    0.19,
                    -0.4
            );
        }
        if (shouldDoNext == ArmFSM.LOAD) {
            armControl.commandArmToState(
                    0.008,
                    -0.25
            );
        }

        if (shouldDoNext == ArmFSM.ACTUATE_GRIPPER) {
            armControl.outtakeGripper();
        } else if (shouldDoNext == ArmFSM.IDLE) {
            armControl.doNothing();
        } else if (shouldDoNext == ArmFSM.DEBUG_TO_DEGREES) {
            armControl.commandArmToState(
                    0, 0
            );

            if (armControl.getErrorQuantity() > Arm.ARM_TOLERANCE_TO_MOVE_ON) {
                shouldDoNext = ArmFSM.IDLE;
            }
        }

        if (shouldDoNext == ArmFSM.SCORE_MID) {

            armControl.commandArmToState(0.008, -0.227);


        }
        if (shouldDoNext == ArmFSM.SCORE_HIGH) {
            //TODO technically upeprAmr should be 0 but because of slack we need to compensate for gravity that FF cant
            armControl.commandArmToState(-0.126, 0.025);


        }
        if (shouldDoNext == ArmFSM.GROUND_INTAKE) {
            armControl.commandArmToState(0.581, -0.274);

        }

        if (shouldDoNext == ArmFSM.UNSTOW) {
            armControl.commandArmToState(-0.1, armControl.upperArm.getMechanismPositionAccum_rot());
        }

        if (shouldDoNext == ArmFSM.HUMAN_INTAKE) {
            armControl.commandArmToState(0.008, -0.230);
        }

        if (shouldDoNext == ArmFSM.IDLE) {
            armControl.doNothing();
        }
        //TODO fill out the rest
    }
}
