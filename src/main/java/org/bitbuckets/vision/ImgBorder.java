package org.bitbuckets.vision;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImgBorder {




        static JFrame frame;
        static JLabel label1;
        static JLabel label2;


        static int numImg = 4;

        static int currIndex = 0;

        public ImgBorder(BufferedImage img) {
            createGUI(img);
        }





        public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
            BufferedImage dbi = null;
            if(sbi != null) {
                dbi = new BufferedImage(dWidth, dHeight, imageType);
                Graphics2D g = dbi.createGraphics();
                AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
                g.drawRenderedImage(sbi, at);
            }
            return dbi;
        }


        public void createGUI(BufferedImage img) {
            frame = new JFrame("FrameDemo");
            label1 = new JLabel();
            label2 = new JLabel("Confidence");


            BufferedImage newImg = scale(img, img.getType(), 1280, 720, (double)1280/img.getWidth(), (double)720/img.getHeight());
            label1.setIcon(new ImageIcon(newImg));
            frame.setSize(newImg.getWidth(), newImg.getHeight());



            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(label1);
            frame.getContentPane().add(label2, BorderLayout.NORTH);


            frame.setResizable(false
            );

            frame.setVisible(true);
        }
        public void updateImg(BufferedImage img, double confidence) {
            BufferedImage newImg = scale(img, img.getType(), 1280, 720, (double)1280/img.getWidth(), (double)720/img.getHeight());
            label1.setIcon(new ImageIcon(newImg));
            label2.setText(""+confidence);
            frame.setSize(newImg.getWidth(), newImg.getHeight());
        }
    }

