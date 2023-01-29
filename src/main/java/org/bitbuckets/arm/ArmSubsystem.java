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

    public void robotPeriodic()
    {
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
                if (armInput.isIntakePressed())
                {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "IntakePosition";

                }
                else if (armInput.isLowPosPressed()){
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "LowPosition";
                }
                else if (armInput.isMidPosPressed())
                {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "MidPosition";
                }
                else if (armInput.isHighPosPressed())
                {
                    state = ArmFSM.POSITION_CONTROL;
                    positionMode = "HighPosition";
                }
                else {
                    System.out.println("Currently in MANUAL");
                    armControl.manuallyMoveLowerArm(armInput.getLowerArm_PercentOutput());
                    armControl.manuallyMoveUpperArm(armInput.getUpperArm_PercentOutput());
                }

                break;
            case POSITION_CONTROL:
                if (armInput.isDisablePositionControlPressed()) {
                    state = ArmFSM.MANUAL;
                }
                else {
                    System.out.println("Currently in POSITION_CONTROL");
                    switch (positionMode) {
                        case "IntakePosition":
                            armControl.moveToIntakePos();
                            System.out.println("Moved to intake position!");
                            break;
                        case "LowPosition":
                            armControl.moveToLowPos();
                            System.out.println("Moved to low position!");
                            break;
                        case "MidPosition":
                            armControl.moveToMidPos();
                            System.out.println("Moved to mid position!");
                            break;
                        case "HighPosition":
                            armControl.movetoHighPos();
                            System.out.println("Moved to high position!");
                            break;
                    }
                }
                break;
        }
        mode.log(positionMode);

    }


}
