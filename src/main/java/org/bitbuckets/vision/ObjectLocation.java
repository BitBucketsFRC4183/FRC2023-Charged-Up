package org.bitbuckets.vision;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ObjectLocation {



        enum ObjectType{
            cone,
            cube,
            placeHolder
        }
        private ObjectType objType;
        private int x;
        private int y;
        private String URL;
        private BufferedImage img;

        public ObjectLocation(ObjectType obj, int x, int y, String URL) {
            objType = obj;
            this.x = x;
            this.y = y;
            this.URL = URL;
            img = null;
        }
        public ObjectLocation(ObjectType obj, int x, int y, BufferedImage img) {
            objType = obj;
            this.x = x;
            this.y = y;
            this.URL = "";
            this.img = img;
        }

        public int[][][] getProfile(int testScale){
            try {
                img = ImageIO.read(new File(URL));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int imgWidth = img.getWidth();
            int imgHeight = img.getHeight();
            //Make sure we aren't near the edge of the picture
            int xDim = Math.max(x, 5*testScale);
            int yDim = Math.max(y, 5*testScale);
            xDim = Math.min(xDim, imgWidth - 5*testScale);
            yDim = Math.min(yDim, imgHeight - 5*testScale);
            int[][][] returns = new int[10][10][3];
            int initX = xDim-5*testScale;
            int initY = yDim-5*testScale;
            for(int i = 0; i < 10 * testScale; i+=testScale) {
                for(int p = 0; p < 10 * testScale; p+=testScale) {
                    Color color = new Color(img.getRGB(initX + i, initY + p), true);
                    returns[i/testScale][p/testScale][0] = color.getRed();
                    returns[i/testScale][p/testScale][1] = color.getGreen();
                    returns[i/testScale][p/testScale][2] = color.getBlue();
                }
            }
            return returns;
        }

        public static ArrayList<int[]> getProfileColors(int[][][] prof){
            ArrayList<int[]> colors = new ArrayList<int[]>();
            for(int i = 0; i < 10; i++) {
                for(int p = 0; p < 10; p++) {
                    int[] currColor = {prof[i][p][0],prof[i][p][1],prof[i][p][2]};
                    boolean isInArray = false;
                    for(int k = 0; k < colors.size(); k++) {
                        int totalDifference = Math.abs(currColor[0] - colors.get(k)[0]) + Math.abs(currColor[1] - colors.get(k)[1]) + Math.abs(currColor[2] - colors.get(k)[2]);
                        isInArray = totalDifference < 30;
                    }

                    if(!isInArray) {
                        colors.add(currColor);
                    }
                }
            }
            return colors;
        }

        public static void printColors(ArrayList<int[]> colors) {
            for(int i = 0; i < colors.size(); i++) {
                for(int p = 0; p < 3; p++) {
                    System.out.print(colors.get(i)[p] + " ");
                }
                System.out.println();
            }
        }

        public static void printProfile(int[][][] prof) {
            for(int i = 0; i < 10; i++) {
                for(int p = 0; p < 10; p++) {
                    for(int l = 0; l < 3; l++) {
                        System.out.println(prof[i][p][l]);
                    }
                }
            }
        }

        public ObjectType getObjectType() {
            return objType;
        }
    }
