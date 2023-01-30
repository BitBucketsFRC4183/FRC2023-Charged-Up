package org.bitbuckets.lib.vendor.spark;

import com.revrobotics.REVLibError;
import edu.wpi.first.wpilibj.DriverStation;

public class RevUtils {

    public static void checkNeoError(REVLibError error, String message) {
        if (error != REVLibError.kOk) {
            DriverStation.reportError(String.format("%s: %s", message, error.toString()), false);
        }
    }

}
