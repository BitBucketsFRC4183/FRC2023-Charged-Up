package org.bitbuckets.auto;

public enum AutoPath {

    //creating auto enums, each corresponds to a space in the PathPlannerTrajectory array,
    //which is used for calling different paths from PathPlanner
    //each of these paths calls from a diff path in IB's pathplanner
    NONE(-1),
    SCORE_TAXI_RIGHT(0),
    SCORE_TAXI_LEFT(1),
    SCORE_TAXI_MID(2),
    SCORE_TAXI_LEFT_SWOOPY(3),
    BACK_1M(4);


    //creates an index to be used for calling each enum as a member of the PathPlannerTrajectory array
    final int index;

    //instatiates the index variable
    AutoPath(int index) {
        this.index = index;
    }

}
