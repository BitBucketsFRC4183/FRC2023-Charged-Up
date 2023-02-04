package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

import java.util.Optional;

public class ElevatorSubsystemSetup implements ISetup<ElevatorSubsystem> {

    final static boolean elevatorEnabled = false;

    private static ElevatorControl buildElevatorControl(ProcessPath path) {
        if (!elevatorEnabled) {
            return MockingUtil.buddy(ElevatorControl.class);
        }
        ElevatorControlSetup elevatorControlSetup = new ElevatorControlSetup(
                new SparkSetup(9, new MotorConfig(ElevatorConstants.getGearRatioExtend, 1, ElevatorConstants.rotToMeterExtend, false, false, 20, false, false, Optional.empty()), new PIDConfig(0, 0, 0, 0)),
                new SparkSetup(10, new MotorConfig(ElevatorConstants.gearRatioTilt, 1, ElevatorConstants.rotToMeterTilt, false, false, 20, false, false, Optional.empty()), new PIDConfig(0, 0, 0, 0))
        );
        ElevatorControl elevatorControl = elevatorControlSetup.build(path.addChild("elevator-control"));
        return elevatorControl;
    }
    @Override
    public ElevatorSubsystem build(ProcessPath path) {
        ElevatorInput elevatorInput = new ElevatorInput(new Joystick(1));
        ElevatorControl elevatorControl = buildElevatorControl(path);

        return new ElevatorSubsystem(elevatorControl, elevatorInput);
    }
}
