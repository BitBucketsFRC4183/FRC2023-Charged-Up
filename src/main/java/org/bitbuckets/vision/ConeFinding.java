package org.bitbuckets.vision;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;



public class ConeFinding {



        public static ImgConfidence getLocCone(BufferedImage img, ArrayList<ObjectLocation> dataSet, int profScale) {
            ArrayList<ImgConfidence> cumList = new ArrayList<>();
            for(int i = 0; i < dataSet.size(); i++){
                if(dataSet.get(i).getObjectType() == ObjectLocation.ObjectType.cone) {
                    int[][][] tempProfile = dataSet.get(i).getProfile(profScale);
                    ArrayList<int[]> colorList = ObjectLocation.getProfileColors(tempProfile);
                    ArrayList<ImgConfidence> tempCumList = compWholeImage(tempProfile, img, colorList, 100, profScale);
                    for(int p = 0; p < tempCumList.size(); p++) {
                        cumList.add(tempCumList.get(p));
                    }
                }
            }
            int bestIndex = 0;
            double bestConfidence = 0;
            for(int i = 0; i < cumList.size(); i++) {
                if(bestConfidence < cumList.get(i).getConfidence()) {
                    bestIndex = i;
                    bestConfidence = cumList.get(i).getConfidence();
                }
            }
            return cumList.get(bestIndex);
        }


        public static ImgConfidence compareProfile(int[][][] prof, BufferedImage img, int initX, int initY, int scale) {
            int sumChange = 0;

            for(int i = 0; i < 10; i+=scale) {
                for(int p = 0; p < 10; p+=scale) {
                    for(int l = 0; l < 3; l++) {
                        ObjectLocation tempPic = new ObjectLocation(ObjectLocation.ObjectType.placeHolder, initX, initY, img);
                        int[][][] tempProfile = tempPic.getProfile(scale);
                        sumChange += Math.abs(prof[i][p][l] - tempProfile[i][p][l]);
                    }
                }
            }

            double pChange = 1 - ((double) sumChange/(10*10*3*255));

            return new ImgConfidence(initX,initY,pChange);
        }

        public static ArrayList<ImgConfidence> compWholeImage(int[][][] prof, BufferedImage img,  ArrayList<int[]> colorList, int scale, int profScale) {
            ArrayList<ImgConfidence> cumList = new ArrayList<ImgConfidence>();
            int width = img.getWidth();
            int height = img.getHeight();
            int range = 30;
            ArrayList<int[]> points = new ArrayList<int[]>();
            while(points.size() ==0) {
                for(int y = 0; y < colorList.size(); y++) {
                    for(int i = 5; i < width - 5; i+=scale) {
                        for(int p = 5; p < height - 5; p+=scale) {
                            Color color = new Color(img.getRGB(i, p), true);
                            int sumChange = Math.abs(colorList.get(y)[0] - color.getRed());
                            sumChange += Math.abs(colorList.get(y)[1] - color.getGreen());
                            sumChange += Math.abs(colorList.get(y)[2] - color.getBlue());
                            if(sumChange < range) {
                                int[] tempInt = {i,p};
                                points.add(tempInt);
                            }
                        }
                    }
                }
                range +=20;
            }

            for(int i = 0; i < points.size(); i++) {
                ImgConfidence tempLocAndProf = null;
                try {
                    tempLocAndProf = compareProfile(prof, img, points.get(i)[0], points.get(i)[1], profScale);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                cumList.add(tempLocAndProf);

            }
            return cumList;
        }
}
