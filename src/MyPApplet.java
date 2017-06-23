import processing.core.*;

import java.security.Timestamp;
import java.util.concurrent.TimeUnit;

public class MyPApplet extends PApplet {

    private String URL = "/Users/taikara/myProjects/javaProgramms/data/palmTrees.jpg";
    private PImage backGroundImg;

    public void setup(){
        size(400, 400);
        backGroundImg = loadImage(URL, "jpg");
    }

    public void draw(){

        backGroundImg.resize(0, height);
        image(backGroundImg, 0, 0);

        int [] fillColor = sunColor();
        fill(fillColor[0], fillColor[1], fillColor[2]);

        ellipse(width/4, height/5, width/5, height/5);
    }

    private int [] sunColor() {
        int[] suncolorVlaues = new int[3];


        int hour   = (int) ((System.currentTimeMillis() / (1000*60*60)) % 24);

        int min = (int)((System.currentTimeMillis()/(1000*60*60))) - (hour*60);

        if (hour >= 5 && hour <= 6) {
            fill(255, 209, 0);
        } else if (hour == 20 && min < 30) {
            fill(255, 255, 0);
        } else if (min > 30 && min < 45 && hour < 21 && hour > 20) {
            fill(189, 190, 192);
        } else {
            fill(255, 209, 0);
        }

        return suncolorVlaues;
    }
}
