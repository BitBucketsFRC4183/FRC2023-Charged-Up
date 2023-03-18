package org.bitbuckets.lib.log;

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;

public class DataUtil {

    public static DataLog LOG;

    static {

        if (HALUtil.getHALRuntimeType() != 2) {
            DataLogManager.start("/media/sda1");
        } else {
            DataLogManager.start("./analysis");
        }

        DataLogManager.logNetworkTables(false);
        LOG = DataLogManager.getLog();

    }


}
