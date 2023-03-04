package org.bitbuckets.vision;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;


public class GamePieceRecognition {




        public static BufferedImage Mat2BufferedImage(Mat mat) throws IOException{
            MatOfByte matB = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, matB);
            byte[] bytes = matB.toArray();
            InputStream input = new ByteArrayInputStream(bytes);
            BufferedImage img = ImageIO.read(input);
            return img;
        }

        public static void drawCircle(BufferedImage img, int x, int y) {
            for(int i = -5; i < 5; i++) {
                img.setRGB(x+i, y, Color.RED.getRGB());
            }
            for(int p = -5; p < 5; p++) {
                img.setRGB(x, y + p, Color.RED.getRGB());
            }
        }
    }

