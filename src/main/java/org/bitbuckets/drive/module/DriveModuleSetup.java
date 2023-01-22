package org.bitbuckets.drive.module;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import org.bitbuckets.drive.fenc.OptimizeEncoderWrapper;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IEncoder;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.log.DataLogger;

public class DriveModuleSetup implements ISetup<DriveModule> {

    final ISetup<IMotorController> driveMotor;
    final ISetup<IMotorController> turnMotor;
    final int canCoderId;
    final double offsetDegrees;

    public DriveModuleSetup(ISetup<IMotorController> driveMotor, ISetup<IMotorController> turnMotor, int canCoderId, double offsetDegrees) {
        this.driveMotor = driveMotor;
        this.turnMotor = turnMotor;
        this.canCoderId = canCoderId;
        this.offsetDegrees = offsetDegrees;
    }

    @Override
    public DriveModule build(ProcessPath path) {

        IMotorController drive = driveMotor.build(path.addChild("drive"));
        IMotorController turn = turnMotor.build(path.addChild("turn"));

        Timer.delay(0.1); //delay because CAN is dumb somtimes
        //real explanation: sometimes invert is set after can stuff is set
        //so we do this in order to block until hopefully the CAN bus receives everything

        //motors are all set up!
        //we now have to do dirty and dumb thingsw

        CANCoderConfiguration config = new CANCoderConfiguration();
        config.sensorCoefficient = 2 * Math.PI / 4096.0;
        config.unitString = "rad";
        config.sensorTimeBase = SensorTimeBase.PerSecond;
        config.magnetOffsetDegrees = offsetDegrees;
        config.enableOptimizations = true;

        WPI_CANCoder cancoder = new WPI_CANCoder(canCoderId);
        cancoder.configAllSettings(config);

        waitForCanCoder(cancoder);
        //okay, cancoder should either be working or our device has exploded. Lets do the dirty.

        forcePositionToEncoder(turn, cancoder.getAbsolutePosition());

        DataLogger<DriveModuleDataAutoGen> logger = path.generatePushDataLogger(DriveModuleDataAutoGen::new);

        DriveModule module = new DriveModule(drive, turn, drive, turn, logger);

        System.out.println("1");

        return module;
    }


    double generateStartingPosition_encoderRadians(IEncoder encoder, double startingPosition_mechanismRadians) {
        return startingPosition_mechanismRadians / encoder.getMechanismFactor();
    }


    void forcePositionToEncoder(IEncoder relativeEncoder, double lastAbsolutePosition_mechanismRadians) {
        double startingPosition_encoderRadians = generateStartingPosition_encoderRadians(relativeEncoder, lastAbsolutePosition_mechanismRadians);
        double encoderSensorUnits = startingPosition_encoderRadians / Math.PI / 2.0 * 2048.0;
        relativeEncoder.forceOffset(encoderSensorUnits); //TODO handle software sided offets
    }

    /**
     * @author team 364
     * @param angryLittleShit
     */
    private static void waitForCanCoder(WPI_CANCoder angryLittleShit){
        /*
         * Wait for up to 1000 ms for a good CANcoder signal.
         *
         * This prevents a race condition during program startup
         * where we try to synchronize the Falcon encoder to the
         * CANcoder before we have received any position signal
         * from the CANcoder.
         */
        int initTime = 0;

        ErrorCode shm = angryLittleShit.getLastError();
        for (int i = 0; i < 100; ++i) {
            angryLittleShit.getAbsolutePosition();

            shm = angryLittleShit.getLastError();
            if (shm.equals(ErrorCode.OK)) {
                DriverStation.reportWarning("init took: " + initTime, false);
                break;
            }
            Timer.delay(0.1);
            initTime += 10;
        }

        System.out.println("how many tume rune " + initTime);


        DriverStation.reportWarning("BAD BAD BAD BAD BAD BAD B" + shm, false);
    }
}
