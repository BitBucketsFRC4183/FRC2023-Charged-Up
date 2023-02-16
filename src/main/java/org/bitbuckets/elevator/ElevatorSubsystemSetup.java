package org.bitbuckets.elevator;

import edu.wpi.first.wpilibj.Joystick;
import org.bitbuckets.auto.AutoSubsystem;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.PIDConfig;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

import java.util.Optional;

public class ElevatorSubsystemSetup implements ISetup<ElevatorSubsystem> {


    final boolean isElevatorEnabled;
    final AutoSubsystem autoSubsystem;

    public ElevatorSubsystemSetup(boolean isElevatorEnabled, AutoSubsystem autoSubsystem) {
        this.isElevatorEnabled = isElevatorEnabled;
        this.autoSubsystem = autoSubsystem;
    }


    @Override
    public ElevatorSubsystem build(IProcess self) {
        if (!isElevatorEnabled) {
            return MockingUtil.buddy(ElevatorSubsystem.class);
        }

        ElevatorInput elevatorInput = new ElevatorInput(new Joystick(1));
        ElevatorControlSetup elevatorControlSetup = new ElevatorControlSetup(
                new SparkSetup(9, new MotorConfig(ElevatorConstants.getGearRatioExtend, 1, ElevatorConstants.rotToMeterExtend, false, false, 20, false, false, Optional.empty()), new PIDConfig(0, 0, 0, 0)),
                new SparkSetup(10, new MotorConfig(ElevatorConstants.gearRatioTilt, 1, ElevatorConstants.rotToMeterTilt, false, false, 20, false, false, Optional.empty()), new PIDConfig(0, 0, 0, 0))
        );

        var debug = self.getDebuggable();

        ElevatorControl elevatorControl = self.childSetup("elevator-control", elevatorControlSetup);
        return new ElevatorSubsystem(elevatorControl, elevatorInput, debug, autoSubsystem);
    }
}
