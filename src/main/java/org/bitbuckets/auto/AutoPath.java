package org.bitbuckets.auto;

public enum AutoPath {

    //creating auto enums, each corresponds to a space in the PathPlannerTrajectory array,
    //which is used for calling different paths from PathPlanner
    //each of these paths calls from a diff path in IB's pathplanner
    NONE(-1),

    SCORE_TAXI_AUTOBALANCE(0),
    taxi_right(1),
    taxi_left(2),
    taxi_middle_dock(3),
    taxi_middle_dock_alt(4),
    SC1_CL1_BL(5),
    SC1_CL1_SC3_BL(6),
    SC9_CL4_BL(7),
    SC9_CL4_SC7_BL(8),
    SC1_CL1_SC3(9),
    SCORE_TAXI_RIGHT(10);


    //creates an index to be used for calling each enum as a member of the PathPlannerTrajectory array
    final int index;

    //instatiates the index variable
    AutoPath(int index) {
        this.index = index;
    }

}
