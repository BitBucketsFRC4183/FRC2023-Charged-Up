package org.bitbuckets.drive.holo;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.control.IPIDCalculator;

public class WorseHoloControlSetup implements ISetup<WorseHoloControl> {

    final ISetup<IPIDCalculator> xPid;
    final ISetup<IPIDCalculator> yPid;
    final ISetup<IPIDCalculator> thetaPid;
    final WorseOdometryControl worseOdometryControl;

    public WorseHoloControlSetup(ISetup<IPIDCalculator> xPid, ISetup<IPIDCalculator> yPid, ISetup<IPIDCalculator> thetaPid, WorseOdometryControl worseOdometryControl) {
        this.xPid = xPid;
        this.yPid = yPid;
        this.thetaPid = thetaPid;
        this.worseOdometryControl = worseOdometryControl;
    }

    @Override
    public WorseHoloControl build(IProcess self) {

        var controller= self.childSetup("theta", thetaPid);

        //TODO why was this removed
        controller.rawAccess(PIDController.class).enableContinuousInput(0, Math.PI * 2.0);

        return new WorseHoloControl(
                self.childSetup("x", xPid),
                self.childSetup("y", yPid),
                controller,
                worseOdometryControl,
                self.getDebuggable());
    }
}
