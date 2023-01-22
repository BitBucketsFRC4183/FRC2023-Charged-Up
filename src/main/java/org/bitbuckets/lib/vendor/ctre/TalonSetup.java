package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.PIDIndex;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.log.StartupLogger;

import static org.bitbuckets.robot.RobotConstants.SET_MAXCONFIGTIME_MS;

/**
 * will generate a sim.
 * dont rely on it.
 * use the SimTalonSetup for an accurate flywheel impl (not here yet)
 */
public class TalonSetup implements ISetup<IMotorController> {

    final int canId;
    final boolean invert;
    final double mechanismFactor;
    final double rotationsToMetersFactor;
    final double currentLimit;
    final double[] pidConstants; //TODO these will be tuneables

    public TalonSetup(int canId, boolean invert, double mechanismFactor, double rotationsToMetersFactor, double currentLimit, double[] pidConstants) {
        this.canId = canId;
        this.invert = invert;
        this.mechanismFactor = mechanismFactor;
        this.rotationsToMetersFactor = rotationsToMetersFactor;
        this.currentLimit = currentLimit;
        this.pidConstants = pidConstants;
    }

    @Override
    public TalonMotorController build(ProcessPath path) {
        //signal for talon boot (find can id)
        StartupLogger ctre_boot = path.generateSignalLogger("ctre-boot");
        //signal for talon configuration
        StartupLogger ctre_config = path.generateSignalLogger("ctre-config");
        StartupLogger log_register = path.generateSignalLogger("ctre-register-loops");

        ctre_boot.signalProcessing();
        WPI_TalonFX talonFX = new WPI_TalonFX(canId); //talon is up!
        ctre_boot.signalCompleted();

        //CONFIGURATION SECTOR
        ctre_config.signalProcessing();

        if (talonFX.configVoltageCompSaturation(12, SET_MAXCONFIGTIME_MS) != ErrorCode.OK) {
            ctre_config.signalErrored("cant config voltage sat");
        }

        talonFX.enableVoltageCompensation(true);

        if (talonFX.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, 0,0), SET_MAXCONFIGTIME_MS) != ErrorCode.OK) {
            ctre_config.signalErrored("cant config supply limit");
        }

        if (talonFX.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, SET_MAXCONFIGTIME_MS) != ErrorCode.OK) {
            ctre_config.signalErrored("cant config integrated sensor on slot 0");
        }

        talonFX.config_kP(0, pidConstants[PIDIndex.P]);
        talonFX.config_kI(0, pidConstants[PIDIndex.I]);
        talonFX.config_kD(0, pidConstants[PIDIndex.D]);

        if (talonFX.getLastError() != ErrorCode.OK) {
            ctre_config.signalErrored("cant config pid");
        }

        //TODO is this right? if we invert sensor phase AND output does pid still work?
        talonFX.setSensorPhase(invert);
        talonFX.setInverted(invert);
        talonFX.setNeutralMode(NeutralMode.Brake);

        ctre_config.signalCompleted();

        DataLogger<TalonDataAutoGen> logger = path.generatePushDataLogger(TalonDataAutoGen::new); //TODO
        TalonMotorController talon = new TalonMotorController(
                talonFX,
                mechanismFactor,
                rotationsToMetersFactor,
                logger
        );


        SimulateMotorController sim = new SimulateMotorController(talonFX, invert);

        log_register.signalProcessing();
        path.registerLoop(talon, "teleop-loop");
        path.registerSimLoop(sim, "simulate-loop");
        log_register.signalCompleted();


        return talon;
    }
}
