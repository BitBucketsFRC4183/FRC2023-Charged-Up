package org.bitbuckets.vision;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;



import static org.bitbuckets.vision.GamePieceRecognition.Mat2BufferedImage;

public class ImageInit {




    static ArrayList<ObjectLocation> list = new ArrayList<>();

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //DataSet
        list.add(new ObjectLocation(ObjectLocation.ObjectType.cone, 2165, 1640, "FILEPATH4.jpg"));
        list.add(new ObjectLocation(ObjectLocation.ObjectType.cone, 969, 589, "FILEPATH5.jpg"));
        list.add(new ObjectLocation(ObjectLocation.ObjectType.cone, 2150, 1500, "FILEPATH6.jpg"));
        list.add(new ObjectLocation(ObjectLocation.ObjectType.cone, 2200, 858, "FILEPATH3.jpg"));
        BufferedImage tempImg = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        ImgBorder imgBorder = new ImgBorder(tempImg);
        while (true) {
            try {
                VideoCapture capture = new VideoCapture(0);

                Mat matrix = new Mat();
                capture.read(matrix);
                //Is Camera Opened?
                if (capture.isOpened()) {
                    //If we can read the Camera
                    if (capture.read(matrix)) {
                        //Convert Mat from OpenCV to BufferedImage
                        BufferedImage img = Mat2BufferedImage(matrix);
                        //Search for cone
                        ImgConfidence result = ConeFinding.getLocCone(img, list, 1);

                        //Draw Locator
                        GamePieceRecognition.drawCircle(img, result.getX(), result.getY());
                        //Refresh Image
                        imgBorder.updateImg(img, result.getConfidence());
                    }
                }

            }
        catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

