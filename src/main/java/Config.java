import org.bitbuckets.lib.ISetup;
import org.bitbuckets.lib.hardware.IMotorController;
import org.bitbuckets.lib.vendor.ctre.TalonDriveMotorSetup;
import org.bitbuckets.lib.vendor.spark.SparkSetup;

public interface Config {

    //DRIVE
    ISetup<IMotorController> FRONT_LEFT_APPA = new TalonDriveMotorSetup();
    ISetup<IMotorController> FRONT_RIGHT_APPA = new TalonDriveMotorSetup();
    ISetup<IMotorController> BACK_LEFT_APPA = new TalonDriveMotorSetup();
    ISetup<IMotorController> BACK_RIGHT_APPA = new TalonDriveMotorSetup();
    ISetup<IMotorController> FRONT_LEFT = new TalonDriveMotorSetup();
    ISetup<IMotorController> FRONT_RIGHT = new TalonDriveMotorSetup();
    ISetup<IMotorController> BACK_LEFT = new TalonDriveMotorSetup();
    ISetup<IMotorController> BACK_RIGHT = new TalonDriveMotorSetup();


}
