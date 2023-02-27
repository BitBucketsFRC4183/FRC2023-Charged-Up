package org.bitbuckets.arm;

public interface ArmConstants {

    // calculated gearRatio
    // Input to output- 5:1 4:1 3:1
    // Final gear different for         each arm; 16:48 for lower and 16:16 for upper


    //change with accurate numbers




    /*
    Starting Configuration
    0 = Horizontal
    lowerJoint = 90
    upperJoint = 0
     */

    // Neo Brushless Motor Relative Encoder: 42 counts per revolution

    // all the numbers below are made up, not actual numbers we will use for competition

    //wrist + elbow for ff purposes

          //  double  elbowCgRadius =
          //  (UPPER_CONFIG.cgRadius * UPPER_ARM.armMass)
            //        + (UPPER_ARM.lengthMeters + config.wrist().cgRadius()) * config.wrist().mass()
            // / (UPPER_ARM.armMass + config.wrist().mass());
   // double elbowMoi =
   //         UPPER_ARM.armMass * Math.pow(UPPER_CGRADIUS - elbowCgRadius, 2.0)
      //              + config.wrist().mass()
       //             * Math.pow(
         //           UPPER_ARM.lengthMeters + config.wrist().cgRadius() - elbowCgRadius, 2.0);


}
