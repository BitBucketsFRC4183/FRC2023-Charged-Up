package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.system.plant.DCMotor;
import org.bitbuckets.lib.hardware.MotorConfig;
import org.bitbuckets.lib.hardware.OptimizationMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

class SparkRelativeMotorControllerTest {

    MotorConfig test = new MotorConfig(
            0.5,
            1,
            1,
            false,
            false,
            20,
            Optional.empty(),
            Optional.empty(),
            false,
            false,
            false, OptimizationMode.GENERIC,
            DCMotor.getNEO(1)
    );


    @Test
    void forceOffset() {
        assert HAL.initialize(500, 0);
        SparkRelativeMotorController c = new SparkRelativeMotorController(test, new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless));

        c.forceOffset(2.0); //2 encoder rotations

        Assertions.assertEquals(2, c.sparkMaxRelativeEncoder.getPosition());
    }

    @Test
    void forceOffset_mechanismRotations() {
        assert HAL.initialize(500, 0);
        SparkRelativeMotorController c = new SparkRelativeMotorController(test, new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless));

        c.forceOffset_mechanismRotations(1.0); //1 mechanism rotation is 2 encoder rotations according to our motor coefficient

        Assertions.assertEquals(2, c.sparkMaxRelativeEncoder.getPosition());

    }

    @Test
    void moveAtVoltage() {
        assert HAL.initialize(500, 0);
        SparkRelativeMotorController c = new SparkRelativeMotorController(test, new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless));

        c.moveAtVoltage(12);

        Assertions.assertSame(c.lastControlMode, LastControlMode.VOLTAGE, "should report voltage mode");
        Assertions.assertEquals(12.0, c.sparkMax.getAppliedOutput(), "applied output should be 12 volts");

    }

    @Disabled
    @Test
    void moveAtPercent() {
        assert HAL.initialize(500, 0);
        SparkRelativeMotorController c = new SparkRelativeMotorController(test, new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless));

        c.moveAtPercent(12);

        Assertions.assertSame(c.lastControlMode, LastControlMode.PERCENT, "should report percent mode");
        Assertions.assertEquals(12.0, c.sparkMax.getAppliedOutput(), "applied output should be 12 volts");

    }

    @Test
    void moveToPosition() {
    }

    @Test
    void moveToPosition_mechanismRotations() {
        CANSparkMax spark = Mockito.mock(CANSparkMax.class);
        Mockito.when(spark.getPIDController()).thenReturn(Mockito.mock(SparkMaxPIDController.class));

        SparkRelativeMotorController c = new SparkRelativeMotorController(test, spark);

        c.moveToPosition_mechanismRotations(1); //should tell the motor to move to 2 encoder rotations if ratio is 2 encoder to 1 motor

        Mockito.verify(c.sparkMaxPIDController).setReference(2.0, CANSparkMax.ControlType.kPosition);

        //verify that the setpoint is reported correctly

        Assertions.assertEquals(1.0, c.getSetpoint_mechanismRotations(), "setpoint should be 1");
    }

    @Test
    void moveAtVelocity() {
    }
}