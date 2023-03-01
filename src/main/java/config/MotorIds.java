package config;

//assorted can IDs that are not mutex'd because can makes 0 sense
public interface MotorIds {

    int PIDGEON_IMU_ID = 5;

    int FRONT_LEFT_DRIVE_ID = 5;
    int FRONT_LEFT_STEER_ID = 6;
    int FRONT_LEFT_ENCODER_CHANNEL = 2;
    int FRONT_RIGHT_DRIVE_ID = 7;
    int FRONT_RIGHT_STEER_ID = 8;
    int FRONT_RIGHT_ENCODER_CHANNEL = 3;
    int BACK_LEFT_DRIVE_ID = 3;
    int BACK_LEFT_STEER_ID = 4;
    int BACK_LEFT_ENCODER_CHANNEL = 1;
    int BACK_RIGHT_DRIVE_ID = 1;
    int BACK_RIGHT_STEER_ID = 2;
    int BACK_RIGHT_ENCODER_CHANNEL = 0;
    int LOWER_ARM_ID_1 = 9;
    int LOWER_ARM_FOLLOWER = 10;
    int UPPER_ARM_ID = 11;
    int GRIPPER_ARM_ID = 12;

    //APPA
    int FRONT_LEFT_DRIVE_APPA = 1;
    int FRONT_LEFT_STEER_APPA = 2;
    int FRONT_LEFT_ENCODER_CHANNEL_APPA = 9;
    int FRONT_RIGHT_DRIVE_APPA = 7;
    int FRONT_RIGHT_STEER_APPA = 8;
    int FRONT_RIGHT_ENCODER_CHANNEL_APPA = 12;
    int BACK_LEFT_DRIVE_APPA = 5;
    int BACK_LEFT_STEER_APPA = 6;
    int BACK_LEFT_ENCODER_CHANNEL_APPA = 11;
    int BACK_RIGHT_DRIVE_APPA = 3;
    int BACK_RIGHT_STEER_APPA = 4;
    int BACK_RIGHT_ENCODER_CHANNEL_APPA = 10;
}
