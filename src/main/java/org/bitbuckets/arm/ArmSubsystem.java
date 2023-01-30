package org.bitbuckets.arm;

import org.bitbuckets.lib.log.ILoggable;

public class ArmSubsystem {

    //make motors


    final ArmInput armInput;
    final ArmControl armControl;
    final ILoggable<String> mode;


    ArmFSM state = ArmFSM.MANUAL;

    private String positionMode;

    public ArmSubsystem(ArmInput armInput, ArmControl armControl, ILoggable<String> mode) {
        this.armInput = armInput;
        this.armControl = armControl;
        this.mode = mode;
    }


    //private double CONTROL_JOINT_OUTPUT = 0.1;

    //calculated gearRatio
    //private double gearRatio = (5 * 4 * 3) / (12. / 30.);

    public void robotPeriodic() {
        //SmartDashboard.putNumber("lowerEncoder",armControl.lowerJoint.getEncoderPositionAccumulated_radians());
        //SmartDashboard.putNumber("upperEncoder",armControl.upperJoint.getEncoderPositionAccumulated_radians());
        //SmartDashboard.putNumber("setpoint",armControl.convertMechanismRotationtoRawRotation_lowerJoint(1));
    }

    public void teleopPeriodic() {


        if (armInput.isCalibratedPressed()) {
            armControl.calibrateLowerArm();
            armControl.calibrateUpperArm();
        }

        switch (state) {
            case MANUAL:
                if (armInput.isIntakeHumanPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "IntakeHuman";

                } else if (armInput.isIntakeGroundPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "IntakeGround";
                } else if (armInput.isScoreMidPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "ScoreMid";
                } else if (armInput.isScoreHighPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "ScoreHigh";
                } else {
                    System.out.println("Currently in MANUAL");
                    armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                    armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());
                }

                break;
            case POSITION_CONTROL:
                if (armInput.isDisablePositionControlPressed()) {
                    state = ArmFSM.MANUAL;
                } else {
                    System.out.println("Currently in POSITION_CONTROL");
                    switch (positionMode) {
                        case "IntakeHuman":
                            armControl.intakeHumanPlayer();
                            break;
                        case "IntakeGround":
                            armControl.intakeGround();
                            break;
                        case "ScoreMid":
                            armControl.scoreMid();
                            break;
                        case "ScoreHigh":
                            armControl.scoreHigh();
                            break;
                    }
                }
                break;
        }
        mode.log(positionMode);

    }


}
