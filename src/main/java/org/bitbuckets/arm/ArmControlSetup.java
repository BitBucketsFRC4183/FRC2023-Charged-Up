package org.bitbuckets.arm;

import com.revrobotics.CANSparkMax;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.log.Debuggable;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;


public class ArmControlSetup implements ISetup<ArmControl> {

    // Lower Device ID = 9
    // Upper Device ID = 3
    final ISetup<IMotorController> lowerJoint;
    final ISetup<IMotorController> upperJoint;


    public ArmControlSetup(ISetup<IMotorController> lowerJoint, ISetup<IMotorController> sparkSetup) {
        this.lowerJoint = lowerJoint;
        this.upperJoint = sparkSetup;

    }


    @Override
    public ArmControl build(ProcessPath self) {

        var lower = lowerJoint.build(self.addChild("lower-joint"));
        var upper = upperJoint.build(self.addChild("upper-joint"));

        var lowerSpark = lower.rawAccess(CANSparkMax.class);
        var upperSpark = upper.rawAccess(CANSparkMax.class);

        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        lowerSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward, true);
        upperSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, true);

        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 27.3);
        lowerSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -13.69);

        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kForward, (float) 25.0);
        upperSpark.setSoftLimit(CANSparkMax.SoftLimitDirection.kReverse, (float) -90.0);

        //Arm Simulation stuff
        /*
        Mechanism2d mech = new Mechanism2d(3, 3);
        // the mechanism root node
        MechanismRoot2d base = mech.getRoot("base", 1.5, 0);
        Mechanism2d upperPivot = getUpperPivot(mech);

        arm = base.append(new MechanismLigament2d("elevator", 2, 90));
        elevatorWrist =
                elevator.append(
                        new MechanismLigament2d("wrist", -0.5, 90, 6, new Color8Bit(Color.kPurple)));

        // post the mechanism to the dashboard
        SmartDashboard.putData("Mech2d", mech);
        var debug = self.generateDebugger();

        //Debuggable debug = self.generateDebugger();
        // ADD DEBUGGABLES
        */

        Debuggable debug = self.generateDebugger();

        return new ArmControl(
                lower,
                upper,
                debug

        );
    }

    private static MechanismRoot2d getUpperPivot(Mechanism2d mech) {
        return mech.getRoot("upperPivot", 1.5, 1);
    }
}

