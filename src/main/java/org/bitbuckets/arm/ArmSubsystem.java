package org.bitbuckets.arm;

public class ArmSubsystem {

    //make motors


    final ArmInput armInput;
    final ArmControl armControl;

    ArmFSM state = ArmFSM.MANUAL;


    public ArmSubsystem(ArmInput armInput, ArmControl armControl) {
        this.armInput = armInput;
        this.armControl = armControl;
    }


    private double CONTROL_JOINT_OUTPUT = 0.1;

    //calculated gearRatio
    private double gearRatio = (5 * 4 * 3) / (12. / 30.);


    public void teleopPeriodic() {

        if (armInput.isCalibratedPressed()) {
            armControl.calibrateLowerArm();
            armControl.calibrateUpperArm();
        }

        switch (state) {
            case MANUAL:
                if (armInput.isIntakePressed() || armInput.isLowPosPressed() || armInput.isMidPosPressed() || armInput.isHighPosPressed()) {
                    state = ArmFSM.POSITION_CONTROL;
                }
                armControl.moveLowerArm(armInput.getLowerArm_PercentOutput());
                armControl.moveUpperArm(armInput.getUpperArm_PercentOutput());

                break;
            case POSITION_CONTROL:
                if (armInput.isDisablePositionControlPressed()) {
                    state = ArmFSM.MANUAL;
                }
                break;
        }


    }


    public void disable() {

    }


}
