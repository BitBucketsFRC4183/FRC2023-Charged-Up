package org.bitbuckets.auto;

import org.bitbuckets.drive.IDriveControl;
import org.bitbuckets.lib.IProcess;
import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.ITuneAs;
import org.bitbuckets.lib.tune.IValueTuner;
import org.bitbuckets.lib.util.MockingUtil;
import org.bitbuckets.odometry.IOdometryControl;

/**
 * labels: low priority, easy
 * TODO: This should use {@link org.bitbuckets.lib.control.IPIDCalculator instead of PidController when it is done}
 */
public class AutoSubsystemSetup implements ISetup<AutoSubsystem> {

    final boolean enabled;
    final ISetup<AutoControl> autoControlSetup;
    final IDriveControl driveControl;
    final IOdometryControl odometryControl;

    public AutoSubsystemSetup(boolean enabled, ISetup<AutoControl> autoControlSetup, IDriveControl driveControl, IOdometryControl odometryControl) {
        this.enabled = enabled;
        this.autoControlSetup = autoControlSetup;
        this.driveControl = driveControl;
        this.odometryControl = odometryControl;
    }

    @Override
    public AutoSubsystem build(IProcess self) {
        if (!enabled) return MockingUtil.buddy(AutoSubsystem.class);

        IAutoControl autoControl = self.childSetup("auto-control", autoControlSetup);
        IValueTuner<AutoPath> pathTuner = self.generateTuner(ITuneAs.ENUM(AutoPath.class), "auto-path", AutoPath.NONE);

        return new AutoSubsystem(pathTuner, autoControl, self.getDebuggable(), driveControl, odometryControl);
    }


}
