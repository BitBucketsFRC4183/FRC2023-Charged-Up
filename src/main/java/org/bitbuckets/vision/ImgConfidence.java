package org.bitbuckets.vision;

public class ImgConfidence {

        private int x;
        private int y;
        private double confidence;
        public ImgConfidence(int x, int y, double confidence) {
            this.x = x;
            this.y = y;
            this.confidence = confidence;
        }
        public double getConfidence() {
            return confidence;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
    }

