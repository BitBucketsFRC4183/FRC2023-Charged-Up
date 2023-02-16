package org.bitbuckets.lib.control;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ProcessPath;
import org.bitbuckets.lib.tune.IValueTuner;

public class ProfiledPIDFSetup implements ISetup<IPIDCalculator> {


    @Override
    public IPIDCalculator build(ProcessPath self) {

        IValueTuner<double[]> tunerForAllValues = self.generateMultiTuner(
                new String[]{"p", "i", "d", "kV", "kA"},
                new double[]{0.0, 0.0, 0.0, 0.0, 0.0}
        );

        double[] initial = tunerForAllValues.readValue();


        //TODO make this use arjun ff

        ProfiledPIDFController controller = new ProfiledPIDFController(initial[0], initial[1], initial[2], 0, new TrapezoidProfile.Constraints(initial[3], initial[4]));
        return new ProfiledPIDFCalculator(controller, tunerForAllValues);
    }
}
