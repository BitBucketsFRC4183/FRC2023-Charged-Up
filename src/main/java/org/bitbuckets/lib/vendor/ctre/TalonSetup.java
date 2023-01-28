package org.bitbuckets.lib.vendor.ctre;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.SetupProfiler;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.hardware.MotorControllerDataAutoGen;
import org.bitbuckets.lib.log.DataLogger;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.log.LoggingConstants;

import static org.bitbuckets.robot.RobotConstants.SET_MAXCONFIGTIME_MS;

/**
 * will generate a sim.
 * dont rely on it.
 * use the SimTalonSetup for an accurate flywheel impl (not here yet)
 */
@Deprecated
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
        SetupProfiler ctre_boot = path.generateSetupProfiler("ctre-boot");
        //signal for talon configuration
        SetupProfiler ctre_config = path.generateSetupProfiler("ctre-config");
        SetupProfiler log_register = path.generateSetupProfiler("ctre-register-loops");

        ctre_boot.markProcessing();
        WPI_TalonFX talonFX = new WPI_TalonFX(canId); //talon is up!
        ctre_boot.markCompleted();

        //CONFIGURATION SECTOR
        ctre_config.markProcessing();

        if (talonFX.configVoltageCompSaturation(12, SET_MAXCONFIGTIME_MS) != ErrorCode.OK) {
            ctre_config.markErrored("cant config voltage sat");
        }

        talonFX.enableVoltageCompensation(true);

        if (talonFX.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, currentLimit, 0, 0), SET_MAXCONFIGTIME_MS) != ErrorCode.OK) {
            ctre_config.markErrored("cant config supply limit");
        }

        if (talonFX.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, SET_MAXCONFIGTIME_MS) != ErrorCode.OK) {
            ctre_config.markErrored("cant config integrated sensor on slot 0");
        }

        if (talonFX.getLastError() != ErrorCode.OK) {
            ctre_config.markErrored("cant config pid");
        }

        //TODO is this right? if we invert sensor phase AND output does pid still work?
        talonFX.setSensorPhase(invert);
        talonFX.setInverted(invert);
        talonFX.setNeutralMode(NeutralMode.Brake);

        ctre_config.markCompleted();

        DataLogger<MotorControllerDataAutoGen> logger = path.generatePushDataLogger(MotorControllerDataAutoGen::new); //TODO
        TalonMotorController talon = new TalonMotorController(
                talonFX,
                mechanismFactor,
                rotationsToMetersFactor,
                logger
        );


        IValueTuner<double[]> valueTuner = path.generateValueTuner("pid", new double[5]);
        TalonSimulationAspect sim = new TalonSimulationAspect(talonFX, invert);
        TalonTuningAspect tuningAspect = new TalonTuningAspect(talonFX, valueTuner);

        log_register.markProcessing();
        path.registerLoop(talon, LoggingConstants.LOGGING_PERIOD, "logging-loop");
        path.registerLoop(tuningAspect, LoggingConstants.TUNING_PERIOD, "tuning-loop" );
        path.registerSimLoop(sim, "simulate-loop");
        log_register.markCompleted();


        return talon;
    }
}
