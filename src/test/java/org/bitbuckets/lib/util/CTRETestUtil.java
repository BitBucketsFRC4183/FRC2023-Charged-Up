package org.bitbuckets.lib.util;

public class CTRETestUtil {

    public static void waitForCTREUpdate() {
        try {
            com.ctre.phoenix.unmanaged.Unmanaged.feedEnable(500);
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
