package org.bitbuckets.lib.util;

/**
 * This class exists because node order is by default parent-to-child down the tree
 *
 * However when considering runnables we want the children to run first since typically when you want your robot to do stuff
 * you want the sensors to have accurate data lmao
 */
public class GuarunteeLoopOrderUtil {

    static Runnable[] orderedRunnableList() {
        throw new UnsupportedOperationException(); //TOOD
    }

}
