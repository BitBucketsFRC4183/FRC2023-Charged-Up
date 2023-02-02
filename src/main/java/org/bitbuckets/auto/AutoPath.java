package org.bitbuckets.auto;

public enum AutoPath {

    //creating auto enums, each corresponds to a space in the PathPlannerTrajectory array,
    //which is used for calling different paths from PathPlanner
    //each of these paths calls from a diff path in IB's pathplanner
    NONE(0),
    AUTO_TEST_PATH_ONE(1),
    AUTO_PATH_TWO(2),
    AUTO_PATH_THREE(3),
    AUTO_PATH_FOUR(4),
    AUTO_PATH_FIVE(5),
    AUTO_PATH_SIX(6),
    AUTO_PATH_SEVEN(7);

    //creates an index to be used for calling each enum as a member of the PathPlannerTrajectory array
    int index;

    //instatiates the index variable
    AutoPath(int index) {
        this.index = index;
    }

}
