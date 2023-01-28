package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;

public class ElevatorControlSetup implements ISetup<ElevatorControl> {

        final ISetup<IMotorController> leftExtend;
        final ISetup<IMotorController> rightExtend;
        final ISetup<IMotorController> leftTilt;

        final ISetup<IMotorController>rightTilt;



    private MechanismLigament2d elevator;

    private MechanismLigament2d elevatorWrist;

    
    public ElevatorControlSetup(ISetup<IMotorController> leftExtend, ISetup<IMotorController> rightExtend, ISetup<IMotorController> leftTilt, ISetup<IMotorController> rightTilt) {
        this.leftExtend = leftExtend;
        this.rightExtend = rightExtend;

        this.leftTilt = leftTilt;
        this.rightTilt = rightTilt;


    }


    @Override
    public ElevatorControl build(ProcessPath path) {


        Mechanism2d mech = new Mechanism2d(3, 3);
        // the mechanism root node
        MechanismRoot2d root = mech.getRoot("pivot", 0, 0);

        // MechanismLigament2d objects represent each "section"/"stage" of the mechanism, and are based
        // off the root node or another ligament object
        elevator = root.append(new MechanismLigament2d("elevator", 2, 90));
        elevatorWrist =
                elevator.append(
                        new MechanismLigament2d("wrist", 0.5, 90, 6, new Color8Bit(Color.kPurple)));

        // post the mechanism to the dashboard
        SmartDashboard.putData("Mech2d", mech);

        ElevatorControl control = new ElevatorControl(
                leftExtend.build(path.addChild("elevator-left-extension")),
                rightExtend.build(path.addChild("elevator-right-extension")),
                leftTilt.build(path.addChild("elevator-left-tilt")),
                rightTilt.build(path.addChild("elevator-right-tilt")),
                elevatorWrist,
                elevator


        );
        return control;
    }
}
